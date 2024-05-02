# Use an official Ubuntu base image
FROM ubuntu:latest

# Set the working directory
ENV PATH="/usr/src/app:/root/.cargo/bin:${PATH}"

COPY ./test_vt_from_desktop.sh .
COPY ./write_ips.sh .
COPY ./test_vt.sh .

RUN chmod a+x ./test_vt_from_desktop.sh
RUN chmod a+x ./write_ips.sh
RUN chmod a+x ./test_vt.sh

RUN apt-get update && apt-get install -y zsh curl jq  gcc wget apache2-utils

RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"

COPY IPS .

CMD "./test_vt.sh"
