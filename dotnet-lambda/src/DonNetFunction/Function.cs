using System;
using System.Collections.Generic;
using System.Text.Json;
using System.Threading.Tasks;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Lambda.APIGatewayEvents;
using Amazon.Lambda.Core;
using Newtonsoft.Json;
using JsonSerializer = Amazon.Lambda.Serialization.Json.JsonSerializer;


// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(JsonSerializer))]

namespace DonNetFunction
{

    public class Function
    {

        private static  AmazonDynamoDBClient _dbClient=new AmazonDynamoDBClient(RegionEndpoint.USEast2);


        public async Task<APIGatewayProxyResponse> FunctionHandler(APIGatewayProxyRequest apigProxyEvent, ILambdaContext context)
        {
            var body = apigProxyEvent.Body;
            Book book =
                System.Text.Json.JsonSerializer.Deserialize<Book>(body);
            book.id = System.Guid.NewGuid().ToString();
            var request = new PutItemRequest
            {
                TableName = "book",
                Item = new Dictionary<string, AttributeValue>()
                {
                    { "id", new AttributeValue { S =book.id }},
                    { "name", new AttributeValue { S = book.name }},
                    { "author", new AttributeValue { S = book.author }}
                }
            };
            await _dbClient.PutItemAsync(request);
            return new APIGatewayProxyResponse
            {
                Body = JsonConvert.SerializeObject(book),
                StatusCode = 201,
                Headers = new Dictionary<string, string> { { "Content-Type", "application/json" } }
            };
        }
    }
}
