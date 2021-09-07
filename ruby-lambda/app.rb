require 'json'
require 'aws-sdk-dynamodb'

 $client = Aws::DynamoDB::Client.new

def create(event:,context:)
  body = event["body"]
  book =JSON.parse(body)
  id = SecureRandom.uuid
  book["id"]=id
 
  table_item = {
    table_name: "book",
    item: book
  }
  $client.put_item(table_item)
  { statusCode: 201, body: JSON.generate(book) }

end