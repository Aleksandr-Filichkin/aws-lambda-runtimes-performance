#!/bin/sh

# set -e

# #build Rust
# cd ./rust-aws-lambda
# sh build.sh
# cd ./../
# ##
# #build Go
# cd ./go-lambda
# sh build.sh
# cd ./../
# #
# #builds Java and GraalVM
# cd ./java-graalvm-lambda
# sh build.sh
# cd ./../
#
#builds .Net 6.0
cd ./dotnet-lambda
. ./build.sh

echo "Container ID: [$containerId]"

aws ecr get-login-password  --region us-east-2 | \
    docker login --username AWS --password-stdin 440457658525.dkr.ecr.us-east-2.amazonaws.com

if [ ! $(aws ecr describe-repositories --output text --query "repositories[?repositoryName == 'lambda-benchmarks'].repositoryName" ) ]
then
    aws ecr create-repository \
        --repository-name lambda-benchmarks \
        --image-tag-mutability IMMUTABLE \
        --image-scanning-configuration scanOnPush=true
fi

echo "Tagging... [${fullName}][${ver}]"
docker tag ${fullName} 440457658525.dkr.ecr.us-east-2.amazonaws.com/lambda-benchmarks:${ver}
echo "Pushing..."
docker push 440457658525.dkr.ecr.us-east-2.amazonaws.com/lambda-benchmarks:${ver}
echo "Pushed."

cd ./../

## Deploy lambdas

# alias sam='sam.cmd'
# sam build --use-container NodeJsFunction -b nodejs
# sam build --use-container RubyFunction -b ruby
#todo move to docker .net lambda
# sam build --use-container DotNetFunction -b dotnet
# sam build DotNetARMFunction -b dotnet-arm

export AWS_ACCESS_KEY_ID=$ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=$SECRET_KEY

# deploy using another template because it refers to another build folder which where built before in docker
# sam deploy -t template.yaml --stack-name dotnet-function  --image-repository 440457658525.dkr.ecr.us-east-2.amazonaws.com/lambda-benchmarks
    # --profile benchmark; 
    # --aws-access-key-id AKIAWNDKSQCO5KU5KVOK \
    # --aws-secret-key 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 

sam deploy --config-file samconfig.toml --parameter-overrides DockerTag=440457658525.dkr.ecr.us-east-2.amazonaws.com/lambda-benchmarks:$ver