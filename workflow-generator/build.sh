mvn clean package
docker build -t nevin160/workflow-generator .
docker push nevin160/workflow-generator
kubectl apply -f app-config-map.yaml
kubectl apply -f service-account.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml