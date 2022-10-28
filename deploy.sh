#!/usr/bin/env bash

case $1 in
    -local)
    ./gradlew build
    docker build . --tag asur:latest
    docker compose up -d
    docker image prune -f
    docker logs -f asur
    ;;
    -prod) ./deploy_server.sh asur 10.0.0.1 22
    ;;
    *) echo "usage ./deploy [-local | -prod ]"
    ;;
esac