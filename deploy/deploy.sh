#!/usr/bin/env bash
set -e
set -o pipefail

export ROOT_DIR="$(cd `dirname $0` && pwd )"

echo "going to deploy Bootiful Loom"
echo "The root directory is ${ROOT_DIR} and the namespace is ${NS_NAME}."

create_ip(){
  ipn=$1
  if [ -z "$ipn" ]; then
    echo "you didn't specify the name of the IP address to create "
  else
    gcloud compute addresses list --format json | jq '.[].name' -r | grep $ipn || gcloud compute addresses create $ipn --global
  fi
}

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

kubectl get ns $NS_NAME || kubectl create namespace $NS_NAME

write_secrets

cd $ROOT_DIR/k8s/carvel/

get_image(){
  kubectl get "$1" -o json  | jq -r  ".spec.template.spec.containers[0].image" || echo "no old version to compare against"
}

for f in bootiful-loom ; do

  echo "------------------"
  Y=app-${f}-data.yml
  D=deployments/${f}-deployment
  OLD_IMAGE=`get_image $D `
  OUT_YML=out.yml
  ytt -f $Y -f "$ROOT_DIR"/k8s/carvel/data-schema.yml -f "$ROOT_DIR"/k8s/carvel/deployment.yml |  kbld -f -  > ${OUT_YML}
  cat ${OUT_YML}
  cat ${OUT_YML} | kubectl apply  -n $NS_NAME -f -
  NEW_IMAGE=`get_image $D`
  echo "comparing container images for the first container!"
  echo $OLD_IMAGE
  echo $NEW_IMAGE
  if [ "$OLD_IMAGE" = "$NEW_IMAGE" ]; then
    echo "no need to restart $D"
  else
   echo "restarting $D"
   kubectl rollout restart $D
  fi

done


cd "$ROOT_DIR"
