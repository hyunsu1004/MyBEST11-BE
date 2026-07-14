package com.best11.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final String model;

    public OpenAiClient(
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model}") String model
    ){
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type","application/json")
                .build();
    }

    @SuppressWarnings("unchecked")
    public String sendMessage(String prompt){
        Map<String,Object> requestBody = Map.of(
                "model",model,
                "messages", List.of(Map.of("role","user","content",prompt)),
                "max_tokens",500
        );

        Map<String,Object> response = restClient.post()
                .uri("/chat/completions")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        List<Map<String,Object>> choices = (List<Map<String,Object>>) response.get("choices");
        Map<String,Object> message = (Map<String,Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
