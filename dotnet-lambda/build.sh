#!/bin/bash

set -e

rm -r -f ./output
mkdir -p ./output

export ver=$(dotnet-gitversion /showvariable ShortSha)
export fullName="dotnet-lambda:${ver}"

echo "docker build --tag ${fullName} ."

docker build -t ${fullName} .

#copy from the docker container to host
export containerId=$(docker create -ti ${fullName} bash)
docker cp ${containerId}:/var/runtime ./output

zip -r function.zip ./output/runtime
