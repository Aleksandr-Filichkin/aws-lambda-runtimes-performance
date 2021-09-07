package com.filichkin.blog.lambda.v3.handler.test;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filichkin.blog.lambda.model.Book;
import com.filichkin.blog.lambda.service.RequestDispatcher;
import com.filichkin.blog.lambda.storage.EnhancedClientBookStorage;
import com.filichkin.blog.lambda.v3.handler.test.model.InvocationResponse;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Slf4j
public class Main {

    private static final String REQUEST_ID_HEADER = "lambda-runtime-aws-request-id";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final RequestDispatcher REQUEST_DISPATCHER = initDispatcher();
    private static final String TABLE_NAME = "book";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();


    private static RequestDispatcher initDispatcher() {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.US_EAST_2).build())
                .build();
        StaticTableSchema<Book> schema = buildDynamodbSchema();
        DynamoDbTable<Book> dynamoDbTable = dynamoDbEnhancedClient.table(TABLE_NAME, schema);
        return new RequestDispatcher(new EnhancedClientBookStorage(dynamoDbTable), OBJECT_MAPPER);
    }

    /**
     * cannot use https://github.com/aws/aws-sdk-java-v2/issues/2445
     */
    private static StaticTableSchema<Book> buildDynamodbSchema() {
        return StaticTableSchema.builder(Book.class)
                .newItemSupplier(Book::new)
                .addAttribute(String.class, a -> a.name("id")
                        .getter(Book::getId)
                        .setter(Book::setId)
                        .tags(primaryPartitionKey()))
                .addAttribute(String.class, a -> a.name("name")
                        .getter(Book::getName)
                        .setter(Book::setName))
                .addAttribute(String.class, a -> a.name("author")
                        .getter(Book::getAuthor)
                        .setter(Book::setAuthor))
                .build();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            String endpoint = System.getenv("AWS_LAMBDA_RUNTIME_API");
            InvocationResponse invocation = getInvocation(endpoint);

            try {
                APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = OBJECT_MAPPER.readValue(invocation.getEvent(), APIGatewayProxyRequestEvent.class);
                APIGatewayProxyResponseEvent apiGatewayProxyResponseEvent = REQUEST_DISPATCHER.dispatch(apiGatewayProxyRequestEvent);

//                 Post to Lambda success endpoint
                HttpRequest request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(apiGatewayProxyResponseEvent)))
                        .uri(URI.create(String.format("http://%s/2018-06-01/runtime/invocation/%s/response", endpoint, invocation.getRequestId())))
                        .build();
                HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                e.printStackTrace();
                handleException(endpoint, invocation, e);
            }
        }
    }

    private static void handleException(String endpoint, InvocationResponse invocation, Exception exception) throws IOException, InterruptedException {


        String errorBody = OBJECT_MAPPER.writeValueAsString(Map.of("error", exception.getMessage()));

        APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
        errorResponse.setStatusCode(500);
        errorResponse.setBody(errorBody);

        // Post to Lambda error endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(errorResponse)))
                .uri(URI.create(String.format("http://%s/2018-06-01/runtime/invocation/%s/error", endpoint, invocation.getRequestId())))
                .build();
        HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static InvocationResponse getInvocation(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("http://%s/2018-06-01/runtime/invocation/next", endpoint)))
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String requestId = response.headers().firstValue(REQUEST_ID_HEADER).orElseThrow();
        return new InvocationResponse(requestId, response.body());
    }
}

