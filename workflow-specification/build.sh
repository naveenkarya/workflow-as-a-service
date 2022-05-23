mvn clean package
docker build -t nevin160/workflow-specification .
docker push nevin160/workflow-specification
kubectl apply -f app-config-map.yaml
kubectl apply -f service-account.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml