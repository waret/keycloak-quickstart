


批量删除已经存在的Album对象
```
sh -x scripts/photoz-test.sh size==20 | jq ".[].id" | awk -F\" '{system("method=DELETE api=/album/"$2" sh -x scripts/photoz-test.sh")}'
```
