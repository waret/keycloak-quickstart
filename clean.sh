#!/bin/bash

docker-compose rm -s -f
docker volume rm keycloak-quickstart_keycloak-datastore
docker network rm keycloak-quickstart_keycloak-quickstart
