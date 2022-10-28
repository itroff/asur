#!/usr/bin/env bash

USER=$1
HOST=$2
PORT=$3

./gradlew build
ssh $USER@$HOST -p $PORT "
mkdir -p asur
"
rsync -avhe "ssh -p $PORT" --progress build/libs/asur-0.0.1-SNAPSHOT.jar $USER@$HOST:~/asur/asur-0.0.1-SNAPSHOT.jar
rsync -avhe "ssh -p $PORT" --progress DockerfileServer $USER@$HOST:~/asur/Dockerfile
rsync -avhe "ssh -p $PORT" --progress docker-compose.yml $USER@$HOST:~/asur/docker-compose.yml
ssh $USER@$HOST -p $PORT "
cd asur
docker build . --tag asur:latest
docker compose up -d
docker logs -f asur
"