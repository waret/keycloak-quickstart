#/bin/bash

sh -x scripts/test.sh api=/api/admin -- parameter-a==claim-value
sh -x scripts/test.sh api=/api/admin -- parameter-a=claim-value
sh -x scripts/test.sh api=/api/admin -- parameter-a:claim-value
sh -x scripts/test.sh username=alice api=/api/admin -- parameter-a:claim-value
sh -x scripts/test.sh username=alice password=alice api=/api/admin -- parameter-a:claim-value
sh -x scripts/test.sh username=alice password=alice api=/api/admin -- parameter-a=claim-value
sh -x scripts/test.sh username=alice password=alice api=/api/admin -- parameter-a==claim-value


sh -x scripts/test.sh username=admin api=/album api_host=http://localhost:8081


sh -x scripts/test.sh username=admin api=/admin/album api_host=http://localhost:8081 rpt=true res_client=resource-photoz
sh -x scripts/test.sh username=alice api=/admin/album api_host=http://localhost:8081 rpt=true res_client=resource-photoz
sh -x scripts/test.sh username=alice api=/admin/album api_host=http://localhost:8081
sh -x scripts/test.sh username=admin api=/admin/album api_host=http://localhost:8081

