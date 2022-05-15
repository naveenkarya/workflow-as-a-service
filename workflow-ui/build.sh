docker build . -t nevin160/workflow-ui
docker push nevin160/workflow-ui
kubectl apply -f workflow-ui-deployment.yaml