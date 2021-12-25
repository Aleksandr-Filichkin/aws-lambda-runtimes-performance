// This example requires the following input to succeed:
// { "command": "do something" }

use lambda_http::{
    handler,
    lambda_runtime::{self, Context, Error},
    Body, IntoResponse, Request, Response,
};
use lazy_static::lazy_static;
use log::LevelFilter;
use rusoto_core::Region;
use rusoto_dynamodb::{AttributeValue, DynamoDb, DynamoDbClient, PutItemInput};
use serde::{Deserialize, Serialize};
use simple_logger::SimpleLogger;
use std::collections::HashMap;
use uuid::Uuid;

// use lifetimes for zero-copy deserialization
#[derive(Serialize, Deserialize, Debug)]
struct Book<'a> {
    id: Option<&'a str>,
    name: &'a str,
    author: &'a str,
}

//use lazy to speed up cold start(use CPU burst on start up)
lazy_static! {
    static ref CLIENT: DynamoDbClient = DynamoDbClient::new(Region::UsEast2);
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
            let book_as_string = handle_body(body).await?;
            Response::builder()
                .status(201)
                .body::<Body>(book_as_string.into())?
        }
        _ => Response::builder().status(400).body("Empty body".into())?,
    })
}

async fn handle_body(body: &str) -> Result<String, Error> {
    let mut book: Book = serde_json::from_str(body)?;

    let book_id = Uuid::new_v4().to_string();
    book.id = Some(&book_id);

    let mut map = HashMap::with_capacity(3);
    map.insert("id".to_string(), AttributeValue { s: Some(book_id.clone()), ..Default::default() });
    map.insert("name".to_string(), AttributeValue { s: Some(book.name.to_string()), ..Default::default() });
    map.insert("author".to_string(), AttributeValue { s: Some(book.author.to_string()), ..Default::default() });

    let put_item = PutItemInput {
        item: map,
        table_name: "book".to_string(),
        ..Default::default()
    };

    CLIENT.put_item(put_item).await?;

    Ok(serde_json::to_string(&book)?)
}
