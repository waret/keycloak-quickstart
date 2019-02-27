#!/bin/bash

keycloak_url=http://keycloak.waret.net
realm=keycloak-quickstart
access_type=public
client_id=public-web
client_secret=secret
username=alice
password=alice
res_client=resource-alice
api_host=http://localhost:8080

function get_token_keycloak_realm_access_client_user() {
    keycloak_url=$1; shift
    realm=$1; shift
    access_type=$1; shift
    client_id=$1; shift
    client_secret=$1; shift
    username=$1; shift
    password=$1; shift
    if [[ $access_type == "public" ]]; then
        http --form POST \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            Accept:application/json \
            Content-Type:application/x-www-form-urlencoded \
            client_id=${client_id} \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' | jq --raw-output '.access_token'
    elif [[ $access_type == "confidential" ]]; then
        http --form POST \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            Accept:application/json \
            Content-Type:application/x-www-form-urlencoded \
            Authorization:" Basic $(printf "${client_id}:${client_secret}" | base64)" \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' | jq --raw-output '.access_token'
    else
            http --form POST \
                ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
                Accept:application/json \
                Content-Type:application/x-www-form-urlencoded \
                client_id=${client_id} \
                client_secret=${client_secret} \
                username=${username} \
                password=${password} \
                grant_type=password \
                response_type='id_token token' \
                scope='openid profile email' | jq --raw-output '.access_token'
    fi
}

function get_rpt_client_user() {
    # keycloak_url=http://keycloak.waret.net
    # realm=keycloak-quickstart
    res_client=$1; shift
    user=$1; shift
    http --form POST \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Accept:application/json \
        Content-Type:application/x-www-form-urlencoded \
        Authorization:" Bearer $(get_token_user $user)" \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client | jq --raw-output '.access_token'
}

function get_token_client_user() {
    # keycloak_url=http://keycloak.waret.net
    # realm=keycloak-quickstart
    # access_type=public
    client_id=$1; shift
    client_secret=$client_id
    usernamed=$1; shift
    password=$usernamed
    get_token_keycloak_realm_access_client_user $keycloak_url $realm $access_type $client_id $client_secret $username $password
}

function get_token_user() {
    # keycloak_url=http://keycloak.waret.net
    # realm=keycloak-quickstart
    # access_type=public
    # client_id=public-web
    # client_secret=public-web
    usernamed=$1; shift
    password=$usernamed
    get_token_keycloak_realm_access_client_user $keycloak_url $realm $access_type $client_id $client_secret $username $password
}

function get_token() {
    # keycloak_url=http://keycloak.waret.net
    # realm=keycloak-quickstart
    # access_type=public
    # client_id=public-web
    # client_secret=public-web
    # usernamed=alice
    # password=alice
    get_token_keycloak_realm_access_client_user $keycloak_url $realm $access_type $client_id $client_secret $username $password
}

function api() {
    # api_host=http://localhost:8080
    user=$1; shift
    api=$1; shift
    http --timeout=300 $api_host/$api Authorization:" Bearer $(get_token_user $user)" $*
    #curl -v -X GET $api_host/$api -H Authorization:"Bearer "$(get_token_user $user)
}

function api_rpt() {
    # api_host=http://localhost:8080
    user=$1; shift
    res_client=$1; shift
    api=$1; shift
    http --timeout=300 $api_host/$api Authorization:" Bearer $(get_rpt_client_user $res_client $user)" $*
}


######### quickstart command

function contract() {
    api alice api/contracts
}

function resourcea() {
    api alice api/resourcea
}

function premium() {
    api alice api/premium
}

function resourceb() {
    api alice api/resourceb
}

function admin() {
    api alice api/admin parameter-a==claim-value
    api alice api/admin parameter-a:claim-value
}

function resourcea_rpt() {
    api_rpt alice resource-alice api/resourcea
}

if [[ $# > 0 ]]; then
    $*
fi
