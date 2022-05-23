## Running the program directly (requires maven)
```
mvn spring-boot:run
```

## Building your own image (Or skip to use existing image: nevin160/workflow-specification)
### Build the project (Requires maven)
```
mvn clean package
```

### Build docker image (requires maven package from previous step)
```
docker build -t <image-name> .
```

### Push image
```
docker login
```
```
docker push <image-name>
```

### Change the image in app-deployment.yaml
```
containers:
  - name: workflow-spec
    image: <image-name>
```
## Running inside kubernetes cluster
```
kubectl apply -f app-config-map.yaml
kubectl apply -f service-account.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml
```
