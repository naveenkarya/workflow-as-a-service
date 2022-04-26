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
1. Create a secret for docker credentials. You can replace n-regcred with any secret name you like.
```
kubectl create secret docker-registry n-regcred --docker-server=https://index.docker.io/v1/ --docker-username=<docker username> --docker-password=<pswd> --docker-email=<email>
```
2. Configure this secret in deployment yaml
```
imagePullSecrets:
      - name: n-regcred
```
3. Configure the docker image in deployment yaml
```
spec:
      containers:
      - name: workflow-ui
        image: nevin160/workflow-ui
```
## Skip creating your own image and run via kubernetes
```
kubectl apply -f workflow-ui-deployment.yaml
```
and then
```
kubectl apply -f workflow-ui-svc.yaml
```
## Testing from the kubernetes cluster
```
kubectl run curl --image=radial/busyboxplus:curl -i --tty
```
and then
```
curl workflow-ui-service:8080/workflowStatus/234
```
  
