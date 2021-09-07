package com.filichkin.blog.lambda.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filichkin.blog.lambda.model.Book;
import com.filichkin.blog.lambda.storage.BookStorage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class RequestDispatcher {
    private final BookStorage bookStorage;
    private final ObjectMapper objectMapper;

    public APIGatewayProxyResponseEvent dispatch(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        try {
            switch (apiGatewayProxyRequestEvent.getHttpMethod()) {
                case "POST":
                    return processPost(apiGatewayProxyRequestEvent);
                case "DELETE":
                    return processDelete(apiGatewayProxyRequestEvent);
                case "PUT":
                    return processPut(apiGatewayProxyRequestEvent);
                case "GET":
                    return processGet(apiGatewayProxyRequestEvent);
                default:
                    return handleException(new UnsupportedOperationException(String.format("Unknown HTTP method '%s'", apiGatewayProxyRequestEvent.getHttpMethod())));
            }
        } catch (Exception e) {
            return handleException(e);
        }

    }

    private APIGatewayProxyResponseEvent processGet(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        Book book = bookStorage.get(getIdFromPath(apiGatewayProxyRequestEvent));
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(200);
        try {
            responseEvent.setBody(objectMapper.writeValueAsString(book));
            return responseEvent;
        } catch (JsonProcessingException e) {
            return handleException(e);
        }
    }

    private APIGatewayProxyResponseEvent processPut(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        try {
            Book book = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), Book.class);
            bookStorage.save(book);
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(200);
            responseEvent.setBody(objectMapper.writeValueAsString(book));
            return responseEvent;
        } catch (JsonProcessingException e) {
            return handleException(e);
        }
    }

    private APIGatewayProxyResponseEvent processDelete(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        bookStorage.delete(getIdFromPath(apiGatewayProxyRequestEvent));
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(204);
        return responseEvent;
    }

    private String getIdFromPath(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        String[] splitPath = apiGatewayProxyRequestEvent.getPath().split("/");
        return splitPath[splitPath.length - 1];
    }

    private APIGatewayProxyResponseEvent processPost(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
        try {
            Book book = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), Book.class);
            book.setId(UUID.randomUUID().toString());
            bookStorage.save(book);
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(201);
            responseEvent.setBody(objectMapper.writeValueAsString(book));
            return responseEvent;
        } catch (JsonProcessingException e) {
            return handleException(e);
        }
    }

    private APIGatewayProxyResponseEvent handleException(Exception e) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(400);
        try {
            responseEvent.setBody(objectMapper.writeValueAsString(Map.of("error", e.getMessage())));
            return responseEvent;
        } catch (JsonProcessingException ex) {
            return responseEvent;
        }
    }

}
