'use strict';

const uuid = require('uuid');
const DynamoDB = require('aws-sdk/clients/dynamodb'); // eslint-disable-line import/no-extraneous-dependencies import/no-internal-modules
const dynamoDb = new DynamoDB.DocumentClient({ region: "us-east-2" });

module.exports.create = async (event, context) => {
  // @see https://docs.aws.amazon.com/lambda/latest/dg/nodejs-context.html
  context.callbackWaitsForEmptyEventLoop = false;

  const { name, author }  = JSON.parse(event.body);

  const params = {
    TableName: 'book',
    Item: {
      id: uuid.v1(),
      name: data.name,
      author: data.author
    },
  };

  // write to the database
  try {
    await dynamoDb.put(params).promise();

    // create a response
    return {
      statusCode: 201,
      body: JSON.stringify(params.Item),
    };
  } catch (e) {
    console.error(e);
    return {
      statusCode: e.statusCode || 501,
      headers: { 'Content-Type': 'text/plain' },
      body: 'Couldn\'t create the todo item.',
    });
  }
};
