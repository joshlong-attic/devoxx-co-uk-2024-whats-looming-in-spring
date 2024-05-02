#!/usr/bin/env zsh

kubectl get services -o json |  jq -r  '.items[].spec.selector.app' | while read svc; do
  IP="$( kubectl  get services/${svc}-service -o json | jq -r '.status.loadBalancer.ingress.[0].ip' )"
  ./test_vt.sh $IP
done