package com.example.bootifulloom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@SpringBootApplication
public class BootifulLoomApplication {

    public static void main(String[] args) {

        System.getenv().forEach((k, v) -> System.out.println(k + ": " + v));

        SpringApplication.run(BootifulLoomApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> httpRoutes(
            @Value("${spring.threads.virtual.enabled}") boolean virtualEnabled,
            CustomerRepository repository) {
        return route()
                .GET("/threads", request -> ok().body(Map.of("spring.threads.virtual.enabled", virtualEnabled)))
                .GET("/customers", request -> ok().body(repository.findAll()))
                .GET("/hello", request -> {
                    Thread.sleep(200);
                    return ok().body(Map.of("message", "Hello, world!"));
                })
                .build();
    }
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}
