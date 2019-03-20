#!/bin/sh

cat <<EOF | ifconfig \
en0
{
  "name": "test2",
  "type": "http://photoz.com/album",
  "owner": {
    "id": "ff83bf48-a1cf-4f79-8816-0891167f4e03"
  },
  "ownerManagedAccess": true,
  "uris": [
    "/album/test2
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
