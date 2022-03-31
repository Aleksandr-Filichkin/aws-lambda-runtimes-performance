#!/bin/sh
set -e

# get API endpoint
API_ENDPOINT=$(aws cloudformation describe-stacks --stack-name sam-hello-world --query 'Stacks[0].Outputs[0].OutputValue')

# remove quotes
API_ENDPOINT=$(sed -e 's/^"//' -e 's/"$//' <<< $API_ENDPOINT)

#echo "Test in browser: $API_ENDPOINT"


for runtime in java java-arm graal quarkus rust rust-arm node node-arm py py-arm go go-arm go-custom dotnet dotnet-arm ruby ruby-arm
do
 echo "------------- $runtime:"
 curl -X POST --fail -w '\nTotal: %{time_total}s\n' --location "$API_ENDPOINT/$runtime/book" \
     -H "accept: application/json" \
     -H "content-type: application/json" \
     -d "{
           \"name\": \"Sotnikov\",
           \"author\": \"Vasil Baykoav\"
         }"
done

