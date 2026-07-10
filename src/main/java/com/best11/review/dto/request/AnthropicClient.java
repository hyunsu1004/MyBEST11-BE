package com.best11.review.dto.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class AnthropicClient {

    private final RestClient restClient;
    private final String model;

    public AnthropicClient(
            @Value("${anthropic.api-key}") String apiKey,
            @Value("${anthropic.model}") String model
    ){
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1")
                .defaultHeader("x-api-key",apiKey)
                .defaultHeader("anthropic-version","2023-06-01")
                .defaultHeader("content-type","application/json")
                .build();
    }
    @SuppressWarnings("unchecked")
    public String sendMessage(String prompt){
        Map<String,Object> requestBody = Map.of(
                "model",model,
                "max_tokens",500,
                "messages", List.of(Map.of("role","user","content",prompt))
        );

        Map<String,Object> response = restClient.post()
                .uri("/messages")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        List<Map<String,Object>> content = (List<Map<String,Object>>) response.get("content");

        return (String) content.get(0).get("text");
    }
}


