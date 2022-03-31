package com.filichkin.blog;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filichkin.blog.lambda.model.Book;
import com.filichkin.blog.lambda.service.RequestDispatcher;
import com.filichkin.blog.lambda.storage.EnhancedClientBookStorage;
import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;


@ApplicationScoped
@RegisterForReflection(targets = {Book.class})
public class Config {
    private static final String TABLE_NAME = "book";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Produces
    public RequestDispatcher initDispatcher() {
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(DynamoDbClient.builder()
                        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                        .region(Region.US_EAST_1).build())
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
}
