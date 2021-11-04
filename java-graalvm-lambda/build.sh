#!/bin/sh
set -e
mkdir -p "output"
mkdir -p "output-arm"
#builds a native binary and zip
docker build  -t java-lambda .

#copy from the docker container to host
containerId=$(docker create -ti java-lambda bash)
docker cp ${containerId}:/tmp/src/lambda-java/target/lambda-java-1.0-SNAPSHOT.jar ./output
docker cp ${containerId}:/tmp/src/lambda-graalvm/target/lambda-graalvm-1.0-SNAPSHOT.jar ./output


#builds a native binary and zip
docker build -f Dockerfile.graalvm -t lambda-graalvm .

#builds a native binary and zip for ARM
docker build -f Dockerfile.graalvm.al-arm -t lambda-graalvm-arm .

#copy from the docker container to host
containerId=$(docker create -ti lambda-graalvm-arm bash)
docker cp ${containerId}:/tmp/dist-arm output-arm

