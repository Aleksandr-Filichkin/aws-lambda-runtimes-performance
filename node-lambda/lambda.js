'use strict';

const uuid = require('uuid');
const DynamoDB = require('aws-sdk/clients/dynamodb'); // eslint-disable-line import/no-extraneous-dependencies import/no-internal-modules
const dynamoDb = new DynamoDB.DocumentClient({ region: "us-east-1" });

module.exports.create = (event, context, callback) => {
  // @see https://docs.aws.amazon.com/lambda/latest/dg/nodejs-context.html
  context.callbackWaitsForEmptyEventLoop = false;
  const data = JSON.parse(event.body);

  const params = {
    TableName: 'book',
    Item: {
      id: uuid.v1(),
      name: data.name,
      author: data.author
    },
  };

  // write  to the database
  dynamoDb.put(params, (error) => {
    // handle potential errors
    if (error) {
      console.error(error);
      callback(null, {
        statusCode: error.statusCode || 501,
        headers: { 'Content-Type': 'text/plain' },
        body: 'Couldn\'t create the todo item.',
      });
      return;
    }

    // create a response
    const response = {
      statusCode: 201,
      body: JSON.stringify(params.Item),
    };
    callback(null, response);
  });
};