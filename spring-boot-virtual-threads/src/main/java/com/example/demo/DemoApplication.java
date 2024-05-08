package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;
import java.util.Optional;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        var threads = Integer.toString(Runtime.getRuntime().availableProcessors());
        System.setProperty("server.tomcat.threads.max", threads);
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", threads);
        System.setProperty("spring.threads.virtual.enabled", Optional
                .ofNullable(System.getenv("VT"))
                .orElse(Boolean.toString(true))
        );
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    RouterFunction<ServerResponse> http(RestClient restClient) {
        return route()
                .GET("/{seconds}", request -> {
                    var result = restClient
                            .get()
                            .uri("http://localhost:9000/delay/" + request.pathVariable("seconds"))
                            .retrieve()
                            .body(String.class);
                    return ok().body(Map.of("thread", Thread.currentThread() + "", "reply", result));
                })
                .build();
    }
}