package com.filichkin.blog.lambda.v3.handler.test.model;

public class InvocationResponse {

    private String requestId;
    private String event;

    public InvocationResponse(String requestId, String event) {
        this.requestId = requestId;
        this.event = event;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
