package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        Map.of(
                "server.tomcat.threads.max", "16",
                "jdk.virtualThreadScheduler.maxPoolSize", "16",
                "spring.threads.virtual.enabled", Boolean.toString(false)
        ).forEach(System::setProperty);

        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}

@Controller
@ResponseBody
class HttpbinClientController {

    private final RestClient restClient;

    HttpbinClientController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/{seconds}")
    Map<String, String> call(@PathVariable int seconds) {
        var result = restClient
                .get()
                .uri("http://localhost:9000/delay/" + seconds)
                .retrieve()
                .body(String.class);

        return Map.of("thread", Thread.currentThread() + "", "reply", result);

    }
}