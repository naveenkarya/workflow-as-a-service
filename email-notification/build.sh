mvn clean package
docker build -t nevin160/email-notification .
docker push nevin160/email-notification
kubectl apply -f app-deployment.yaml