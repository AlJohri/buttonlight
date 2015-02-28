# ButtonLight

## Get List

```
curl http://buttonlight.herokuapp.com/status/
curl http://buttonlight.herokuapp.com/status/?device_id=53ff6b066678505521351367
curl http://buttonlight.herokuapp.com/status/?device_id=53ff6b066678505521351367&last=1
```

## Set Status
```
curl -X POST http://buttonlight.herokuapp.com/status/ -d 'status=1'
```
