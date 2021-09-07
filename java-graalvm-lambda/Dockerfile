FROM maven:3.8.2-openjdk-11-slim

# 2. Copy the files in your machine to the Docker image
WORKDIR /tmp/
COPY . .


RUN cd /tmp/src/ && mvn clean install
