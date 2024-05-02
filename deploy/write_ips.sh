#!/usr/bin/env bash

IPS_FN=$GITHUB_WORKSPACE/deploy/IPS
kubectl get services -o json |  jq -r  '.items[].spec.selector.app' | while read svc; do
  IP="$( kubectl  get services/${svc}-service -o json | jq -r '.status.loadBalancer.ingress.[0].ip' )"
  echo $IP >> $IPS_FN
done
