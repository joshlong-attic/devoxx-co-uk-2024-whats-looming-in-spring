FROM ubuntu:24.04

ARG APP_NAME
ENV APP_NAME $APP_NAME

RUN apt-get update
RUN apt-get install -y ca-certificates
RUN rm -rf /var/lib/apt/lists/*

COPY target/${APP_NAME} /app
CMD ["/app"]
