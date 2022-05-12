## Running the program (requires maven)
```
mvn spring-boot:run
```

## Build the project (Requires maven)
```
mvn clean package
```

## Build docker image (requires maven package from previous step)
```
docker build -t <image-name> .
```

## Push image
```
docker login
```
```
docker push <image-name>
```

## Running in kubernetes cluster
```
kubectl apply -f db-config.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml
```
