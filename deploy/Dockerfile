# Use an official Ubuntu base image
FROM ubuntu:latest

# Set the working directory
WORKDIR /usr/src/app
ENV PATH="/usr/src/app:${PATH}"
ENV PATH="/root/.cargo/bin:${PATH}"

# Copy the script into the container
COPY test_vt.sh  ./
COPY setup_test_vt.sh   ./

# Make the script executable
RUN chmod +x ./test_vt.sh
RUN chmod +x ./setup_test_vt.sh
RUN ./setup_test_vt.sh

# Run zsh by default
CMD ["zsh", "./test_vt.sh"]
