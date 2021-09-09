#!/bin/sh
set -e

mkdir -p "output"

#builds a native binary and zip
docker build  -t go-lambda .

#copy from the docker container to host
containerId=$(docker create -ti go-lambda bash)
docker cp ${containerId}:/go/src/app/lambda-go ./output