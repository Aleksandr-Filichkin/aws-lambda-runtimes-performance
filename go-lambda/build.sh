#!/bin/sh
set -e

mkdir -p "output"
mkdir -p "output-arm"
mkdir -p "output-custom-runtime"
#builds a native binary and zip
docker build  -t go-lambda .

#copy from the docker container to host
containerId=$(docker create -ti go-lambda bash)
docker cp ${containerId}:/go/src/app/lambda-go ./output

#builds a native binary and zip
docker build --file Dockerfile.arm -t go-lambda-arm .

#copy from the docker container to host
containerId=$(docker create -ti go-lambda-arm bash)
docker cp ${containerId}:/go/src/app/function.zip ./output-arm

#builds a native binary and zip
docker build --file Dockerfile.custom-runtime -t go-lambda-custom-runtime .

#copy from the docker container to host
containerId=$(docker create -ti go-lambda-custom-runtime bash)
docker cp ${containerId}:/go/src/app/function.zip ./output-custom-runtime