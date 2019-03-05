#!/bin/bash

[ -z $keycloak_url ] && keycloak_url=http://localhost:8180
[ -z $realm ] && realm=keycloak-quickstart
[ -z $access_type ] && access_type=public
[ -z $client_id ] && client_id=public-web
[ -z $client_secret ] && client_secret=secret
[ -z $username ] && username=alice
[ -z $password ] && password=$username
[ -z $res_client ] && res_client=resource-photoz
[ -z $api_host ] && api_host=http://localhost:8081
[ -z $api ] && api=/album
[ -z $rpt ] && rpt=false
[ -z $method ] && method=

function get_token_keycloak_realm_access_client_user() {
    if [[ $access_type == "public" ]]; then
        http --form \
            --ignore-stdin \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            Accept:application/json \
            client_id=${client_id} \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' | jq --raw-output '.access_token'
    elif [[ $access_type == "confidential" ]]; then
        http --form \
            --ignore-stdin \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            Accept:application/json \
            Authorization:" Basic $(printf "${client_id}:${client_secret}" | base64)" \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' | jq --raw-output '.access_token'
    else
            http --form \
                --ignore-stdin \
                ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
                Accept:application/json \
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
    http --form \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Accept:application/json \
        Authorization:" Bearer $(get_token_keycloak_realm_access_client_user)" \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client | jq --raw-output '.access_token'
}

if $rpt; then
    http --timeout=300 $method $api_host$api $@ Authorization:" Bearer $(get_rpt_token_keycloak_realm_access_client_user)"
    #curl -v -X GET $api_host$api $@ -H Authorization:"Bearer "$(get_token_keycloak_realm_access_client_user)
else
    http --timeout=300 $method $api_host$api $@ Authorization:" Bearer $(get_token_keycloak_realm_access_client_user)"
fi
