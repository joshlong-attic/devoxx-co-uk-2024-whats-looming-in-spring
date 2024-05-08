package com.example.ai;

import org.springframework.ai.chat.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@ResponseBody
@SpringBootApplication
public class AiApplication {

    public AiApplication(ChatClient singularity) {
        this.singularity = singularity;
    }

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }

    private final ChatClient singularity;

    @GetMapping("/story")
    Map<String, String> story() {
        var prompt = """
            
            
            Dear Singularity, 
            
            Please tell me a story about the amazing Java and Spring Boot 
            developers in London. 
            
            And, please do so in the style of famed children's author, 
            Dr. Seuss.
            
            Cordially,
            Josh
            
            
            		
                """;
        var reply = this.singularity.call(prompt);
        return Map.of("story", reply);
    }

}
