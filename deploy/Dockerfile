# Use an official Ubuntu base image
FROM ubuntu:latest

# Set the working directory
WORKDIR /usr/src/app

# Install necessary packages
RUN apt-get update && apt-get install -y \
    zsh \
    curl \
    jq \
    gnupg \
    lsb-release \
    kubectl \
    && rm -rf /var/lib/apt/lists/*

## Install kubectl
#RUN curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add - && \
#    echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" > /etc/apt/sources.list.d/kubernetes.list && \
#    apt-get update && apt-get install -y kubectl

# Install Rust and then oha
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"
RUN cargo install oha

# Copy the script into the container
COPY test_vt.sh  ./

# Make the script executable
RUN chmod +x your-script.zsh

# Run zsh by default
CMD ["zsh", "./test_vt.sh"]
