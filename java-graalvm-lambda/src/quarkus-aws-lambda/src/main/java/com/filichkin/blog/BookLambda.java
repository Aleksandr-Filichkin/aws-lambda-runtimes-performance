package com.filichkin.blog;

import javax.inject.Inject;
import javax.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.filichkin.blog.lambda.service.RequestDispatcher;

@Named("book")
public class BookLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Inject
    RequestDispatcher requestDispatcher;


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        return requestDispatcher.dispatch(apiGatewayProxyRequestEvent);
    }
}
