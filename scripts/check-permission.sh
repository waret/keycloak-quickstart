#!/bin/bash

[ -z $keycloak_url ] && keycloak_url=http://localhost:8180
[ -z $realm ] && realm=keycloak-quickstart
[ -z $access_type ] && access_type=public
[ -z $client_id ] && client_id=public-web
[ -z $client_secret ] && client_secret=secret
[ -z $username ] && username=alice
[ -z $password ] && password=$username
[ -z $res_client ] && res_client=resource-photoz
[ -z $res_client_secret ] && res_client_secret=secret
[ -z $api_host ] && api_host=http://localhost:8082
[ -z $api ] && api=/album
[ -z $rpt ] && rpt=false
[ -z $method ] && method=
[ -z $permission ] && permission=52d9537b-d12a-41b7-8985-e36ae1a508a8#album:view,album:create

function get_user_access_token() {
    if [[ $access_type == "public" ]]; then
        http --form \
            --ignore-stdin \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            client_id=${client_id} \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' \
            | jq --raw-output '.access_token'
    elif [[ $access_type == "confidential" ]]; then
        # 通过 header 传递 client 认证信息
        http --form \
            --ignore-stdin \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            Authorization:" Basic $(printf "${client_id}:${client_secret}" | base64)" \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' \
            | jq --raw-output '.access_token'
    else
        # 通过 body 传递 client 认证信息
        http --form \
            --ignore-stdin \
            ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
            client_id=${client_id} \
            client_secret=${client_secret} \
            username=${username} \
            password=${password} \
            grant_type=password \
            response_type='id_token token' \
            scope='openid profile email' \
            | jq --raw-output '.access_token'
    fi
}

function get_client_access_token() {
    # 通过 body 传递 client 认证信息，等价于 get_client_access_token2
    http --form \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        grant_type=client_credentials \
        client_id=${res_client} \
        client_secret=${res_client_secret} \
        | jq --raw-output '.access_token'
}

function get_client_access_token2() {
    # 通过 header 传递 client 认证信息，等价于 get_client_access_token
    http --form \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        grant_type=client_credentials \
        Authorization:" Basic $(printf "${res_client}:${res_client_secret}" | base64)" \
        | jq --raw-output '.access_token'
}

function get_user_rpt() {
    # 基于 user access token，获取 rpt，token 里包含所有可访问资源的 authorization
    http --form \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Authorization:" Bearer $(get_user_access_token)" \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client \
        | jq --raw-output '.access_token'
}

function get_user_rpt2() {
    # 基于 user access token 和指定的资源，获取 rpt
    http --form \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Authorization:" Bearer $(get_user_access_token)" \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client \
        permission=$permission \
        | jq --raw-output '.access_token'
}

function get_user_rpt3() {
    # 基于 user access token，以及 client 的认证信息，判断用户是否具有访问指定资源的权限
    http --form \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/token \
        Authorization:" Basic $(printf "${res_client}:${res_client_secret}" | base64)" \
        subject_token=$(get_user_access_token) \
        grant_type=urn:ietf:params:oauth:grant-type:uma-ticket \
        audience=$res_client \
        permission=$permission \
        | jq --raw-output '.access_token'
}

function get_resource_id() {
    # 首先获取 client access token，然后基于该 token 从 resource server 端查询所有的resource
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set \
        Authorization:" Bearer $(get_client_access_token)" \
        matchingUri==false \
        deep==false
}

function get_resource_deep() {
    # 首先获取 client access token，然后基于该 token 从 resource server 端查询所有的resource
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set \
        Authorization:" Bearer $(get_client_access_token)" \
        matchingUri==false \
        deep==true
}

function get_resource_deep2() {
    # 首先获取 client access token，然后基于该 token 从 resource server 端查询所有的resource
    client_access_token=$(get_client_access_token)
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set \
        Authorization:" Bearer $client_access_token" \
        matchingUri==false \
        deep==false \
        | jq -r '.[]' \
        | while read line; do \
            http \
                --ignore-stdin \
                ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set/$line \
                Authorization:" Bearer $client_access_token"
        done
}

function get_resource_deep3() {
    res_id=$1
    client_access_token=$(get_client_access_token)
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set/$res_id \
        Authorization:" Bearer $client_access_token"
}

function get_uma2_configuration() {
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/.well-known/uma2-configuration \
        | jq
}

function get_cert() {
    http \
        --ignore-stdin \
        ${keycloak_url}/auth/realms/${realm}/protocol/openid-connect/certs \
        | jq
}

function create_resource() {
    name=$1
    cat <<EOF | http \
        POST \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set \
        content-type:application/json \
        Authorization:" Bearer $(get_client_access_token)"
{
  "name": "$name",
  "type": "http://photoz.com/album",
  "owner": {
    "id": "ff83bf48-a1cf-4f79-8816-0891167f4e03"
  },
  "ownerManagedAccess": true,
  "uris": [
    "/album/$name"
  ],
  "scopes": [
    {
      "name": "album:view"
    },
    {
      "name": "album:delete"
    }
  ]
}
EOF
}

function create_resource2() {
    name=$1
    http \
        POST \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set \
        content-type:application/json \
        Authorization:" Bearer $(get_client_access_token)" <<<"
{
  \"name\": \"$name\",
  \"type\": \"http://photoz.com/album\",
  \"owner\": {
    \"id\": \"ff83bf48-a1cf-4f79-8816-0891167f4e03\"
  },
  \"ownerManagedAccess\": true,
  \"uris\": [
    \"/album/$name\"
  ],
  \"scopes\": [
    {
      \"name\": \"album:view\"
  },
    {
      \"name\": \"album:delete\"
  }
  ]
}
"
}

function update_resource() {
    http \
        PUT \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set/9224b267-579d-4991-bfbb-0ea7995e72bd \
        content-type:application/json \
        Authorization:" Bearer $(get_client_access_token)" <<<"
{
  \"name\": \"waret\",
  \"type\": \"http://photoz.com/album\",
  \"owner\": {
    \"id\": \"ff83bf48-a1cf-4f79-8816-0891167f4e03\",
    \"name\": \"alice\"
  },
  \"ownerManagedAccess\": true,
  \"attributes\": {},
  \"_id\": \"9224b267-579d-4991-bfbb-0ea7995e72bd\",
  \"uris\": [
    \"/album/waret03\",
    \"/album/waret\"
  ],
  \"scopes\": [
    {
      \"id\": \"b674f901-2a1d-420c-90b9-d6e9fcfcf180\",
      \"name\": \"album:view\"
    },
    {
      \"id\": \"740f5643-7ab1-409f-b8c6-f8c738c4c97d\",
      \"name\": \"album:delete\"
    }
  ]
}
"
}

function add_permission_rpt() {
    # 如果没有下面的参数，则返回 403 failed: Client application [public-web] is not registered as a resource server.
    client_id=resource-photoz
    access_type=confidential
    cat <<EOF | http \
        POST \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/uma-policy/9224b267-579d-4991-bfbb-0ea7995e72bd \
        content-type:application/json \
        Authorization:" Bearer $(get_user_rpt)"
{
        "name": "Any people manager 5",
        "description": "Allow access to any people manager 5",
        "roles": ["user"]
}
EOF
}

function query_permission_user() {
    client_id=resource-photoz
    access_type=confidential
    http \
        GET \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/uma-policy \
        Authorization:" Bearer $(get_user_access_token)" \
        resource==9224b267-579d-4991-bfbb-0ea7995e72bd
}

function add_permission_user() {
    client_id=resource-photoz
    access_type=confidential
    cat <<EOF | http \
        POST \
        ${keycloak_url}/auth/realms/${realm}/authz/protection/uma-policy/9224b267-579d-4991-bfbb-0ea7995e72bd \
        content-type:application/json \
        Authorization:" Bearer $(get_user_access_token)"
{
        "name": "Any people manager 4",
        "description": "Allow access to any people manager 4",
        "roles": ["user"]
}
EOF
}

#{
#  "type": "resource",
#  "logic": "POSITIVE",
#  "decisionStrategy": "UNANIMOUS",
#  "name": "waret-permission",
#  "description": "waret-permission",
#  "resources": [
#    "9224b267-579d-4991-bfbb-0ea7995e72bd"
#  ],
#  "policies": [
#    "a8c7fbf2-f604-4593-90ff-b7567c15f739"
#  ]
#}

function delete_resource() {
    res_id=$1
    http \
        DELETE\
        ${keycloak_url}/auth/realms/${realm}/authz/protection/resource_set/$res_id \
        Authorization:" Bearer $(get_user_rpt)"
}


# TODO 创建 policy 接口

# TODO 创建 permission 接口

# TODO 更新 resource 信息接口

# TODO resource Attribute 相关接口

#echo $(get_user_rpt | cut -d . -f 2 | base64 -D)"}" | jq --raw-output '.authorization'
#get_user_rpt2 | cut -d . -f 2 | base64 -D | jq --raw-output '.authorization'
#get_user_rpt3 | cut -d . -f 2 | base64 -D | jq --raw-output '.authorization'
#get_resource_id
#get_resource_deep
#get_uma2_configuration
#get_cert
#create_resource
#create_resource2
#delete_resource $*

$*
