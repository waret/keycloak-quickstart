#!/bin/bash
#curl -s -X POST http://keycloak.waret.net/auth/realms/spring-boot-quickstart/protocol/openid-connect/token \
#    -H 'Authorization: Basic YXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdDpzZWNyZXQ=' \
#    -H 'content-type: application/x-www-form-urlencoded' \
#    -d 'username=alice&password=alice&grant_type=password' | jq --raw-output '.access_token'

function get_token() {
    username=$1
    password=$2

    # Client Access Type: confidential
    # http --form POST http://keycloak.waret.net/auth/realms/spring-boot-quickstart/protocol/openid-connect/token Authorization:'Basic YXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdDpzZWNyZXQ=' Content-Type:application/x-www-form-urlencoded username=alice password=alice grant_type=password response_type='id_token token' scope='openid profile email' | jq --raw-output '.access_token'
    # http --form POST http://keycloak.waret.net/auth/realms/spring-boot-quickstart/protocol/openid-connect/token Content-Type:application/x-www-form-urlencoded client_id=app-authz-rest-springboot client_secret=secret username=alice password=alice grant_type=password response_type='id_token token' scope='openid profile email' | jq --raw-output '.access_token'

    # Client Access Type: public。使用 id_token 无法访问后端服务
    # tutorial-frontend
    # http --form POST http://keycloak.waret.net/auth/realms/Demo-Realm/protocol/openid-connect/token Accept:application/json Content-Type:application/x-www-form-urlencoded client_id=tutorial-frontend username=$username password=$password grant_type=password response_type='id_token token' scope='openid profile email' | jq --raw-output '.access_token'
    # web-public
    http --form POST http://keycloak.waret.net/auth/realms/spring-boot-quickstart/protocol/openid-connect/token Accept:application/json Content-Type:application/x-www-form-urlencoded client_id=web-public username=$username password=$password grant_type=password response_type='id_token token' scope='openid profile email' | jq --raw-output '.access_token'
}

function get_token_alice() {
    get_token alice alice
}

function get_token_admin() {
    get_token admin admin
}

function get_token_jdoe() {
    get_token jdoe jdoe
}

function get_token_by_user() {
    get_token_$1
}

function contract() {
    http --timeout=300 http://localhost:8080/api/contracts Authorization:"Bearer $(get_token_by_user $1)"
}

function resourcea() {
    http --timeout=300 http://localhost:8080/api/resourcea Authorization:"Bearer $(get_token_by_user $1)"
    #curl -v -X GET http://localhost:8080/api/resourcea -H Authorization:"Bearer "$(get_token_by_user $1)
}

function premium() {
    http http://localhost:8080/api/premium Authorization:"Bearer $(get_token_by_user $1)"
}

function resourceb() {
    http http://localhost:8080/api/resourceb Authorization:"Bearer $(get_token_by_user $1)"
}

function admin() {
    #http http://localhost:8080/api/admin Authorization:"Bearer $(get_token_by_user $1)"
    # Header
    http http://localhost:8080/api/admin Authorization:"Bearer $(get_token_by_user $1)" parameter-a:claim-value
    # application/x-www-form-urlencoded parameters
    #http http://localhost:8080/api/admin Authorization:"Bearer $(get_token_by_user $1)" parameter-a=claim-value
    # URL parameters (?q=search)
    http http://localhost:8080/api/admin Authorization:"Bearer $(get_token_by_user $1)" parameter-a==claim-value
}
