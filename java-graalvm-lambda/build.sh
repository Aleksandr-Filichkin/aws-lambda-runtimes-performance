#!/bin/sh
set -e

#builds a native binary and zip
docker build  -t java-lambda .

#copy from the docker container to host
containerId=$(docker create -ti java-lambda bash)
docker cp ${containerId}:/tmp/src/lambda-java/target/lambda-java-1.0-SNAPSHOT.jar ./output
docker cp ${containerId}:/tmp/src/lambda-graalvm/target/lambda-graalvm-1.0-SNAPSHOT.jar ./output


#builds a native binary and zip
docker build -f Dockerfile.graalvm -t lambda-graalvm .

#copy from the docker container to host
containerId=$(docker create -ti lambda-graalvm bash)
docker cp ${containerId}:/tmp/dist output

