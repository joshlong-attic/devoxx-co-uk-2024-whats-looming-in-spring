package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;

import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@Controller
@ResponseBody
class DemoController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String url;

    private final RestClient restClient;

    private final boolean virtualThreads  ;

    DemoController(Environment environment, @Value("${spring.threads.virtual.enabled:false}") boolean vt,
                   @Value("${httpbin.url}") String url, RestClient.Builder builder) {
        this.restClient = builder.build();
        this.virtualThreads = vt;
        this.url = url;
        log.info("virtual threads enabled? "  + vt );
    }

    @GetMapping("/vt")
    boolean threads() {
        return this.virtualThreads ;
    }

    @GetMapping("/block/{seconds}")
    Map<String, String> block(@PathVariable Integer seconds) {
        // Make a blocking call (network I/O --> thread-per-request, synchronous operation)
        var resolvedUrl = url + "/delay/" + seconds;
        log.debug("resolved url: {}", resolvedUrl);
        var result = restClient
                .get()
                .uri(resolvedUrl)
                .retrieve()
                .toEntity(String.class);

        log.debug("{} on {}", result.getStatusCode(), Thread.currentThread());

        return Map.of("thread", Thread.currentThread() + "");
    }


}