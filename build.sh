#!/bin/sh
set -e

#build Rust
cd ./rust-aws-lambda
sh build.sh
cd ./../
##
#build Go
cd ./go-lambda
sh build.sh
cd ./../
#
#builds Java and GraalVM
cd ./java-graalvm-lambda
sh build.sh
cd ./../

## Deploy lambdas

alias sam='sam.cmd'
sam build --use-container NodeJsFunction -b nodejs
sam build --use-container RubyFunction -b ruby
#todo move to docker .net lambda
sam build DotNetFunction -b dotnet
sam build DotNetARMFunction -b dotnet-arm



# deploy using another template because it refers to another build folder which where built before in docker
sam deploy -t template-final.yaml
