# syntax=docker/dockerfile:1
FROM mcr.microsoft.com/dotnet/sdk:3.1 AS build-env
WORKDIR /app

# Copy csproj and restore as distinct layers
#COPY *.csproj ./
RUN #dotnet restore

# Copy everything else and build
COPY . .
RUN dotnet publish -c Release -o output