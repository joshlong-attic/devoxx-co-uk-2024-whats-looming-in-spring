# Use an official Ubuntu base image
FROM ubuntu:latest

# Set the working directory
WORKDIR /usr/src/app
ENV PATH="/usr/src/app:${PATH}"

# Install necessary packages
RUN apt-get update && apt-get install -y \
    zsh \
    curl \
    jq \
    gnupg \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

## Install kubectl
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" > /usr/src/app


# Install Rust and then oha
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"
RUN cargo install oha

# Copy the script into the container
COPY test_vt.sh  ./

# Make the script executable
RUN chmod +x ./test_vt.sh

# Run zsh by default
CMD ["zsh", "./test_vt.sh"]
