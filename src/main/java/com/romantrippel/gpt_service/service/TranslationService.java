package com.romantrippel.gpt_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.romantrippel.gpt_service.connector.OpenAIConnector;
import com.romantrippel.gpt_service.dto.TranslationResponse;
import com.romantrippel.gpt_service.dto.TranslationResponse.Example;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final OpenAIConnector openAIConnector;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<TranslationResponse> translateWord(String word) {
        String prompt = """
                Translate the English word "%s" to Russian. 
                Provide exactly 3 example sentences in English using this word, and for each give its Russian translation.
                Respond strictly in JSON like this:
                {
                  "translation": "russian_word",
                  "examples": [
                    {"en": "example sentence 1", "ru": "translation 1"},
                    {"en": "example sentence 2", "ru": "translation 2"},
                    {"en": "example sentence 3", "ru": "translation 3"}
                  ]
                }
                """.formatted(word);

        return openAIConnector.requestChatCompletion(prompt)
                .map(this::parseJsonResponse);
    }

    private TranslationResponse parseJsonResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            String translation = root.path("translation").asText();
            List<Example> examples = new ArrayList<>();
            for (JsonNode node : root.path("examples")) {
                examples.add(new Example(
                        node.path("en").asText(),
                        node.path("ru").asText()
                ));
            }
            return new TranslationResponse(translation, examples);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON from OpenAI: " + e.getMessage(), e);
        }
    }
}
