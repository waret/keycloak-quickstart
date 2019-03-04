#!/bin/bash

keycloak_url=http://localhost:8180
realm=keycloak-quickstart
access_type=public
client_id=public-web
client_secret=secret
username=alice
password=alice
res_client=resource-photoz
api_host=http://localhost:8081
api=/album
rpt=false

function get_token_keycloak_realm_access_client_user() {
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

function get_rpt_token_keycloak_realm_access_client_user() {
    http --form POST \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Accept:application/json \
        Content-Type:application/x-www-form-urlencoded \
        Authorization:" Bearer $(get_token_keycloak_realm_access_client_user)" \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client | jq --raw-output '.access_token'
}

function api() {
    http --timeout=300 $api_host$api Authorization:" Bearer $(get_token_keycloak_realm_access_client_user)" $@
    #curl -v -X GET $api_host$api -H Authorization:"Bearer "$(get_token_keycloak_realm_access_client_user)
}

function api_rpt() {
    http --timeout=300 $api_host$api Authorization:" Bearer $(get_rpt_token_keycloak_realm_access_client_user)" $@
}

if [[ $# > 0 ]]; then
    while [[ $# > 0 ]]; do
        if [[ $1 != "--" ]]; then
            eval $1
            if echo $1 | grep -q username; then
                password=$username
            fi
            shift
        else
            shift
            break
        fi
    done
    
    if $rpt; then
        api_rpt $@
    else
        api $@
    fi
fi
