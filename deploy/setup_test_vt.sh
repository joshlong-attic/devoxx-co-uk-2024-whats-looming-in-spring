#!/usr/bin/env bash

# Install necessary packages
apt-get update && apt-get install -y \
    zsh \
    curl \
    jq \
    gnupg \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

## Install kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" > /usr/src/app


# Install Rust and then oha
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
export PATH="/root/.cargo/bin:${PATH}"
cargo install oha