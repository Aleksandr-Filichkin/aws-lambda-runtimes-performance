// This example requires the following input to succeed:
// { "command": "do something" }

use log::LevelFilter;
use serde::{Deserialize, Serialize};
use simple_logger::SimpleLogger;
use rusoto_core::{Region};
use rusoto_dynamodb::{DynamoDb, DynamoDbClient, PutItemInput, AttributeValue, PutItemError, PutItemOutput};
use std::collections::HashMap;
use uuid::Uuid;
use lazy_static::lazy_static;
use lambda_http::{handler, lambda_runtime::{self, Context, Error}, IntoResponse, Request, Body, Response};

#[derive(Serialize, Deserialize, Debug)]
struct Book {
    id: Option<String>,
    name: String,
    author: String,
}

//use lazy to speed up cold start(use CPU burst on start up)
lazy_static! {
   static ref CLIENT:DynamoDbClient=DynamoDbClient::new(Region::UsEast2);
}


#[tokio::main(flavor = "current_thread")]
async fn main() -> Result<(), Error> {
    SimpleLogger::new().with_level(LevelFilter::Info).init().unwrap();
    lambda_runtime::run(handler(func)).await?;
    Ok(())
}

async fn func(event: Request, _: Context) -> Result<impl IntoResponse, Error> {
    Ok(match event.body() {
        Body::Text(body) => {
            let book_as_string = serde_json::to_string(&handle_body(body).await)?;
             Response::builder()
                .status(201)
                .body::<Body>(book_as_string.into())?
        },
        _ =>
             Response::builder()
                .status(400)
                .body("Empty body".into())?
        ,
    })
}

async fn handle_body(body: &String) -> Book {
    let mut book: Book = serde_json::from_str(body.as_str()).unwrap();
    let mut map = HashMap::new();
    let uuid = Uuid::new_v4().to_string();
    book.id = Some(uuid.clone());
    map.insert("id".to_string(), AttributeValue { s: Some(book.id.clone().unwrap()), ..AttributeValue::default() });
    map.insert("name".to_string(), AttributeValue { s: Some(book.name.clone()), ..AttributeValue::default() });
    map.insert("author".to_string(), AttributeValue { s: Some(book.author.clone()), ..AttributeValue::default() });

    let put_item = PutItemInput {
        item: map,
        table_name: "book".to_string(),
        ..Default::default()
    };
    let save_result = CLIENT.put_item(put_item).await;
    match save_result {
        Err(e) => println!("Cannot save book {}, due to {}", uuid, e.to_string()),
        _ => {}
    }
    book
}
