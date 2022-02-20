#!/bin/sh
set -e

# get API endpoint
API_ENDPOINT=$(aws cloudformation describe-stacks --stack-name sam-hello-world --query 'Stacks[0].Outputs[0].OutputValue')

# remove quotes
API_ENDPOINT=$(sed -e 's/^"//' -e 's/"$//' <<< $API_ENDPOINT)

echo "Test in browser: $API_ENDPOINT"