#!/bin/sh
set -e


#builds a native binary and zip
docker build  -t dotnet-lambda .

#copy from the docker container to host
containerId=$(docker create -ti dotnet-lambda)
docker cp ${containerId}:/app/output ./



