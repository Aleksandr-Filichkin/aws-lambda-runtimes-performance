#!/bin/sh
set -e

#build Rust
cd ./rust-aws-lambda
sh build.sh
cd ./../
##
##build Go
cd ./go-lambda
sh build.sh
cd ./../
#
#builds Java and GraalVM
cd ./java-graalvm-lambda
sh build.sh
cd ./../


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
