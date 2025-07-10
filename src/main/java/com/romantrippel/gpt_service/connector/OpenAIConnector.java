package com.romantrippel.gpt_service.connector;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class OpenAIConnector {

    private final WebClient webClient;
    private final String apiKey;

    public OpenAIConnector(WebClient.Builder webClientBuilder,
                           @Value("${openai.api-key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .build();
        this.apiKey = apiKey;
    }

    public Mono<String> requestChatCompletion(String prompt) {
        var body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.at("/choices/0/message/content").asText());
    }
}