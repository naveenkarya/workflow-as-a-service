## To create your own docker image and run in kubernetes

### Build docker image
```
docker build . -t <username>/<modulename>
```
### Testing docker image
Assuming your application listens to 8080 port.
```
docker run -p 8080:8080 -d <username>/<modulename>
```
### Changes required for kubernetes

Configure the docker image in deployment yaml
```
spec:
      containers:
      - name: workflow-ui
        image: <username>/<modulename>
```
## Skip creating your own image and use the existing one (nevin160/workflow-ui)
```
kubectl apply -f workflow-ui-deployment.yaml
```

## Testing from within the kubernetes cluster
```
kubectl run curl --image=radial/busyboxplus:curl -i --tty
```
and then
```
curl workflow-ui-service:8080/workflowStatus/234
```
  
