FROM scratch

ADD 0f967e2d8c237dbb2136bce924ae248aabb9713ea4d3b03c8fbcaeec2800619b.tar.xz /
ADD 32d9d4a89dc9e0819abe6c8648af96d2a019a72acf8398e00d411c8a809caa29.tar.xz /
ADD 3b3c77be8fdd622e10a66f337922a2befceb644c27670f14575ca5e8d2791515.tar.xz /
ADD 485c13c0dd3cd0702e17d995f294cf62c4d44ace07c51629681a0c8b9c183b0a.tar.xz /
ADD 4980c640ef1414062089ced5b7cbbce3acb13f489f3bcc500ab65eee7758de3e.tar.xz /
ADD 72d7e67bee849fd6631889dbad62d8e9cbadd74072b73707a6906e5ad60d5d22.tar.xz /
ADD 8083e7edcd838f555ea16b422d377376cb82f452845c7d4868327a15b91858ff.tar.xz /
ADD 9882e2c0b9128fab2c7fad5173cc2caf3e61ed106244b79bd055654d4f234afc.tar.xz /

ENV LANG=en_US.UTF-8
ENV TZ=:/etc/localtime
ENV PATH=/var/lang/bin:/usr/local/bin:/usr/bin/:/bin:/opt/bin
ENV LD_LIBRARY_PATH=/var/lang/lib:/lib64:/usr/lib64:/var/runtime:/var/runtime/lib:/var/task:/var/task/lib:/opt/lib
ENV LAMBDA_TASK_ROOT=/var/task
ENV LAMBDA_RUNTIME_DIR=/var/runtime
ENV DOTNET_VERSION=5.0.11
ENV DOTNET_RUNNING_IN_CONTAINER=true
ENV DOTNET_ROOT=/var/lang/bin
ENV DOTNET_NOLOGO=true
ENV DOTNET_CLI_TELEMETRY_OPTOUT=true
ENV url=https://download.visualstudio.microsoft.com/download/pr/17b6759f-1af0-41bc-ab12-209ba0377779/e8d02195dbf1434b940e0f05ae086453/dotnet-sdk-6.0.100-linux-x64.tar.gz

WORKDIR /var/src
COPY src/DotNetFunction .

RUN ls -al

RUN yum clean all && yum install -y tar gzip;

RUN curl -o dotnet-sdk-6.0.100-linux-x64.tar.gz --verbose $url; \
    mkdir -p $HOME/dotnet && tar zxf dotnet-sdk-6.0.100-linux-x64.tar.gz -C $HOME/dotnet; \
    export DOTNET_ROOT=$HOME/dotnet; \
    export PATH=$PATH:$HOME/dotnet:$HOME/.dotnet/tools; \
    $HOME/dotnet/dotnet --version; \
    $HOME/dotnet/dotnet publish . \
    --output "/var/runtime" \
    --configuration "Release" \
    --framework "net6.0" \
    --self-contained true \
    /p:GenerateRuntimeConfigurationFiles=true \
    --runtime linux-x64

WORKDIR /var/runtime

RUN ls -al

ENTRYPOINT ["/lambda-entrypoint.sh"]
CMD ["bootstrap::DotNetFunction.Function::FunctionHandler']
