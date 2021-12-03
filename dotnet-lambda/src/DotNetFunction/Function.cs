using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Lambda.APIGatewayEvents;
using Amazon.Lambda.Core;
using Amazon.Runtime.Internal;


// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace DotNetFunction
{

    public class Function
    {
        private static readonly AmazonDynamoDBClient _dbClient = new AmazonDynamoDBClient(RegionEndpoint.USEast2);

        public static async Task<APIGatewayProxyResponse> FunctionHandler(
            APIGatewayProxyRequest apigProxyEvent, 
            ILambdaContext context)
        {
            try
            {
                var body = apigProxyEvent.Body;
                System.Console.WriteLine($"Entered FunctionHandler\n[{body}]");
                Book book =
                    System.Text.Json.JsonSerializer.Deserialize<Book>(body);
                book.id = System.Guid.NewGuid().ToString();
                System.Console.WriteLine($"book.id: [{book.id}]");
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
                System.Console.WriteLine($"request: [{request}]");
                var resp = await _dbClient.PutItemAsync(request);

                Console.WriteLine($"Database response: {resp.HttpStatusCode}");

                return new APIGatewayProxyResponse
                {
                    Body = System.Text.Json.JsonSerializer.Serialize(book),
                    StatusCode = 201,
                    Headers = new Dictionary<string, string> { { "Content-Type", "application/json" } }
                };
            }
            catch(Exception ex)
            {
                context.Logger.LogLine(ex.ToString());
                Console.WriteLine($"Caught Exception: {ex}");
                ex.Data.Add("Body", apigProxyEvent.Body);
                throw;
            }
        }
    }
}
