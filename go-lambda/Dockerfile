FROM golang:1.16

WORKDIR /go/src/app
COPY . .

RUN go get -d -v ./...
RUN go install -v ./...

RUN GOARCH=amd64 GOOS=linux go build -o lambda-go


CMD ["lambda-go"]


