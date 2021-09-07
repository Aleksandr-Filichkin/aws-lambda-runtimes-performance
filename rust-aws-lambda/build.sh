#!/bin/sh
set -e

#builds a native binary and zip
docker build  -t rust-lambda .


#copy from the docker container to host
containerId=$(docker create -ti rust-lambda bash)
docker cp ${containerId}:function.zip ./output

