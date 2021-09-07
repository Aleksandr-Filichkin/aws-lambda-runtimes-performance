package com.filichkin.blog.lambda.storage;

import com.filichkin.blog.lambda.model.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@AllArgsConstructor
@Getter
public class EnhancedClientBookStorage implements BookStorage {

    private final DynamoDbTable<Book> bookTable;

    @Override
    public void save(Book book) {
         bookTable.putItem(book);
    }

    @Override
    public Book get(String id) {
        return bookTable.getItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public Book delete(String id) {
        return bookTable.deleteItem(Key.builder().partitionValue(id).build());
    }

    @Override
    public Book update(Book book) {
        return bookTable.updateItem(book);
    }
}
