package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TestDemoApplication {

    record Run(GenericContainer genericContainer, ConfigurableApplicationContext context) {
    }

    record RunResult(Run run, String oha, boolean virtualThreadsEnabled, double liveThreads) {
    }

    static Run runBootApplication(boolean virtualThreadsEnabled) throws Exception {
        var port = 8080;
        var httpbin = new GenericContainer<>("mccutchen/go-httpbin")
                .withAccessToHost(true)
                .withExposedPorts(port)
                .waitingFor(new WaitAllStrategy());
        httpbin.start();

        //
        var threads = Integer.toString(Runtime.getRuntime().availableProcessors());
        System.setProperty("server.tomcat.threads.max", threads);
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", threads);

        //
        System.setProperty("spring.threads.virtual.enabled", Boolean.toString(virtualThreadsEnabled));
        //
        var url = "http://" + httpbin.getHost() + ":" + httpbin.getMappedPort(port);
        System.setProperty("httpbin.url", url);

        return new Run(httpbin, SpringApplication.run(DemoApplication.class));
    }

    public static void main(String[] args) throws Exception {
        var http = RestClient.builder().build();
        var runs = new ArrayList<Run>();
        var measures = new ArrayList<RunResult>();
        for (var virtualThreadsEnabled : Set.of(true, false)) {
            var run = runBootApplication(virtualThreadsEnabled);
            runs.add(run);
            if (run.context() instanceof WebServerApplicationContext webServerApplicationContext) {
                var port = webServerApplicationContext.getWebServer().getPort();
                var url = "http://localhost:" + port + "/";
                var activeThreads = (Double) ((List<Map<String, Object>>) http
                        .get()
                        .uri(url + "actuator/metrics/jvm.threads.live").retrieve()
                        .toEntity(Map.class)
                        .getBody()
                        .get("measurements"))
                        .getFirst()
                        .get("value");

                var exec = Runtime.getRuntime().exec(" oha -c 20 -n 60 --no-tui " + url + "block/3 ");
                try (var i = exec.getInputStream(); var ir = new InputStreamReader(i);) {
                    var output = FileCopyUtils.copyToString(ir);
                    measures.add(new RunResult(run, output, virtualThreadsEnabled, activeThreads));
                }

                run.context().close();
            }
        }

        for (var rr : measures) {
            System.out.println(System.lineSeparator());
            System.out.println("-------------------");
            System.out.println(rr.liveThreads());
            System.out.println(rr.virtualThreadsEnabled());
            System.out.println(rr.oha());
        }
    }
}
