#/bin/bash

sh -x test.sh api=/api/admin -- parameter-a==claim-value
sh -x test.sh api=/api/admin -- parameter-a=claim-value
sh -x test.sh api=/api/admin -- parameter-a:claim-value
sh -x test.sh username=alice api=/api/admin -- parameter-a:claim-value
sh -x test.sh username=alice password=alice api=/api/admin -- parameter-a:claim-value

