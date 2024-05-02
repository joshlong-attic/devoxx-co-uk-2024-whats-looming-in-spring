#!/usr/bin/env bash
set -e
set -o pipefail

cd $GITHUB_WORKSPACE

echo "going to deploy Bootiful Loom"
echo "The root directory is ${GITHUB_WORKSPACE} and the namespace is ${NS_NAME}."



write_secrets(){
  export SECRETS=${NS_NAME}-secrets
  SECRETS_FN=$HOME/${SECRETS}
  mkdir -p "$( dirname $SECRETS_FN)"
  cat <<EOF >"${SECRETS_FN}"
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}
DB_HOST=${DB_HOST}
DB_SCHEMA=${DB_SCHEMA}
DEBUG=true
EOF

  kubectl delete secrets -n $NS_NAME $SECRETS || echo "no secrets to delete."
  kubectl create secret generic $SECRETS -n $NS_NAME --from-env-file $SECRETS_FN
}

#kubectl get ns $NS_NAME || kubectl create namespace $NS_NAME
#write_secrets

echo "------------------"


# build and deploy container
APP_NAME=bootiful-loom
IMAGE_NAME=us-docker.pkg.dev/${GCLOUD_PROJECT}/mogul-artifact-registry/${APP_NAME}:latest
# todo
#./mvnw -DskipTests spring-boot:build-image  -Dspring-boot.build-image.imageName=$IMAGE_NAME
#docker push $IMAGE_NAME
## /todo

for APP_NAME in bootiful-loom-with-vt bootiful-loom-without-vt ; do
  YAML=${APP_NAME}.yml
  DEPLOYMENT=deployments/${APP_NAME}-deployment
  echo "deploying ${DEPLOYMENT} ..."
  kubectl delete $DEPLOYMENT || echo "could not delete the deployment $DEPLOYMENT "
  ytt -f $GITHUB_WORKSPACE/deploy/$YAML -f $GITHUB_WORKSPACE/deploy/data-schema.yml -f $GITHUB_WORKSPACE/deploy/deployment.yml |  kbld -f  - | kubectl apply  -n $NS_NAME -f -

done



