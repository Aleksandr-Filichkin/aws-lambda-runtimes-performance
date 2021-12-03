using System;
using System.Threading.Tasks;
using Amazon.Lambda.APIGatewayEvents;
using Amazon.Lambda.Core;
using Amazon.Lambda.RuntimeSupport;
using Amazon.Lambda.Serialization.SystemTextJson;


namespace DotNetFunction
{
    public static class Program
    {
        public static async Task Main(string[] _)
        {
            Func<APIGatewayProxyRequest, ILambdaContext, Task<APIGatewayProxyResponse>> 
                func = Function.FunctionHandler;
            using var handlerWrapper = HandlerWrapper.GetHandlerWrapper(func, new DefaultLambdaJsonSerializer());
            using var bootstrap = new LambdaBootstrap(handlerWrapper);
            await bootstrap.RunAsync();
        }
    }
}
