#!/bin/sh
set -e

#builds Rust
#cd ./rust-aws-lambda
#sh build.sh
#cd ./../
#
##builds Rust
#cd ./go-lambda
#sh build.sh
#cd ./../
#
##builds Java and GraalVM
#cd ./java-graalvm-lambda
#sh build.sh
#cd ./../

#sam deploy --guided

##build go lambda
#cd ./go-lambda
#GOARCH=amd64 GOOS=linux go build -o ./../lambda-go
#
##build node lambda
#cd ./../node-lambda/
#npm install
#


#cd ./../
## Deploy lambdas
#cd ../
alias sam='sam.cmd'
sam build --use-container NodeJsFunction
sam build --use-container RubyFunction

#todo move to docker .net lambda
sam build DotNetFunction
#
#sam deploy
