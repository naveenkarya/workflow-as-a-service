mvn clean package
docker build -t nevin160/scheduler-service .
docker push nevin160/scheduler-service
kubectl apply -f app-deployment.yaml