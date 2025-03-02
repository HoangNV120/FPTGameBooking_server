package com.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class SensitiveWordFilterUtil {

    @Value("${gemini.apiKey}")
    private String apiKey;
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SensitiveWordFilterUtil() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        this.apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
    }

    public String filterSensitiveWords(String inputText) {
        String prompt = "Analyze the following text and return a JSON array of explicit, offensive, or inappropriate words ONLY. Do not include names, brands, or neutral words. Ensure the output is in JSON format. Text: \""
                + inputText + "\".";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
        System.out.println(responseEntity.getBody());
        return extractSensitiveWords(responseEntity.getBody());
    }

    private String extractSensitiveWords(String jsonResponse) {
        try {
            Map<?, ?> responseMap = objectMapper.readValue(jsonResponse, Map.class);
            List<?> candidates = (List<?>) responseMap.get("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                List<?> parts = (List<?>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                    String text = (String) part.get("text");
                    text = text.replace("```json", "").replace("```", "").trim();
                    return text.substring(1, text.length() - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String censorWords(String text, String sensitiveWords) {
        String[] words = sensitiveWords.replaceAll("[\\[\\]\"]", "").split(",");

        for (String word : words) {
            word = word.trim();
            if (word.isEmpty()) continue;

            String regex = "(?iu)(?<!\\pL)" + Pattern.quote(word) + "(?!\\pL)";

            StringBuilder censored = new StringBuilder();
            for (char c : word.toCharArray()) {
                if (c == ' ') {
                    censored.append(" ");
                } else {
                    censored.append("*");
                }
            }

            text = text.replaceAll(regex, censored.toString());
        }
        return text;
    }
}
