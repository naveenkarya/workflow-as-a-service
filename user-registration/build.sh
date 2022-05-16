docker build -t nevin160/flask-dock:registration .
docker push nevin160/flask-dock:registration
kubectl apply -f app-deployment.yaml