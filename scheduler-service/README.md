## Running the program (requires maven)
```
mvn spring-boot:run
```

## Build the project (Requires maven)
```
./mvnw install
```

## Build docker image (requires maven package from previous step)
```
docker build -t scheduler-service.jar .
```

## Push image
```
docker login
```
```
docker push scheduler-service.jar
```

## Running in kubernetes cluster
```
kubectl apply -f db-config.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml
```
