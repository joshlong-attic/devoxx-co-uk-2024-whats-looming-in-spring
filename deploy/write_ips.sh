#!/usr/bin/env bash

IPS_FN=$GITHUB_WORKSPACE/deploy/IPS

touch $IPS_FN

kubectl get services -o json |  jq -r  '.items[].spec.selector.app' | while read svc; do
  IP="$( kubectl  get services/${svc}-service -o json | jq -r ".status.loadBalancer.ingress[].ip"   )"
  echo $IP >> $IPS_FN
done

echo "--------"
cat $IPS_FN
echo "--------"

