package com.filichkin.blog.lambda.v2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filichkin.blog.lambda.model.Book;
import com.filichkin.blog.lambda.service.RequestDispatcher;
import com.filichkin.blog.lambda.storage.EnhancedClientBookStorage;

public class EnhancedRequestDispatcher extends RequestDispatcher {
    public EnhancedRequestDispatcher(EnhancedClientBookStorage enhancedClientBookStorage, ObjectMapper objectMapper) {
        super(enhancedClientBookStorage, objectMapper);
    }

    public void warmUp(){
        String warmUpValue="warmUp";
        Book warmUpBook = Book.builder().id(warmUpValue).name(warmUpValue).build();
        //warm up the storage
        getBookStorage().update(warmUpBook);
    }
}
