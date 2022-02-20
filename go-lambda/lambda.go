package main

import (
	"github.com/aws/aws-lambda-go/events"
	"github.com/aws/aws-lambda-go/lambda"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbattribute"
	"github.com/aws/aws-sdk-go/service/dynamodb/dynamodbiface"
	"github.com/google/uuid"

	"encoding/json"
	"fmt"
)

var (
	svc dynamodbiface.DynamoDBAPI
)

type Book struct {
	Id     string `json:"id,omitempty"`
	Author string `json:"author"`
	Name   string `json:"name"`
}

func Handler(request events.APIGatewayProxyRequest) (events.APIGatewayProxyResponse, error) {

	// New uuid for item id
	itemUuid := uuid.New().String()

	// Unmarshal to Item to access object properties
	itemString := request.Body
	itemStruct := Book{}
	json.Unmarshal([]byte(itemString), &itemStruct)

	// Create new item of type item
	item := Book{
		Id:     itemUuid,
		Author: itemStruct.Author,
		Name:   itemStruct.Name,
	}

	// Marshal to dynamobb item
	av, err := dynamodbattribute.MarshalMap(item)
	if err != nil {
		fmt.Println("Error marshalling item: ", err.Error())
		return events.APIGatewayProxyResponse{StatusCode: 500}, nil
	}

	tableName := "book"

	input := &dynamodb.PutItemInput{
		Item:      av,
		TableName: aws.String(tableName),
	}

	// PutItem request
	_, err = svc.PutItem(input)

	// Checking for errors, return error
	if err != nil {
		fmt.Println("Got error calling PutItem: ", err.Error())
		return events.APIGatewayProxyResponse{StatusCode: 500}, nil
	}

	// Marshal item to return
	itemMarshalled, err := json.Marshal(item)

	//Returning response with AWS Lambda Proxy Response
	return events.APIGatewayProxyResponse{Body: string(itemMarshalled), StatusCode: 201}, nil
}

func main() {
	// Creating session for client
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		Config:            aws.Config{Region: aws.String("us-east-1")},
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create DynamoDB client
	svc = dynamodb.New(sess)
	lambda.Start(Handler)
}
