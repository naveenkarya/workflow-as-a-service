mvn clean package
docker build -t nevin160/workflow-generator .
docker push nevin160/workflow-generator
kubectl apply -f db-config.yaml
kubectl apply -f db-deployment_pv.yaml
kubectl apply -f app-deployment.yaml