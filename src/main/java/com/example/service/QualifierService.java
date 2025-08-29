package com.example.service;

import com.example.dto.FinalQueryPayload;
import com.example.dto.GenerateWebhookRequest;
import com.example.dto.GenerateWebhookResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Service
public class QualifierService {

    private final WebClient webClient;

    public QualifierService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    // Step 1: Call generateWebhook API
    public GenerateWebhookResponse generateWebhook(GenerateWebhookRequest req) {
        return webClient.post()
                .uri("https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class)
                .block();
    }

    // Step 2: Load SQL query from resources depending on regNo last 2 digits
    public String pickFinalQueryFromResources(String regNo) {
        int lastTwo = extractLastTwoDigits(regNo);
        String file = (lastTwo % 2 == 0) ? "sql/question2.sql" : "sql/question1.sql";

        try {
            var resource = new ClassPathResource(file);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read SQL file: " + file, e);
        }
    }

    private int extractLastTwoDigits(String regNo) {
        // Extract numeric part from regNo (e.g., REG12347 -> 47)
        String digits = regNo.replaceAll("\\D+", "");
        if (digits.length() < 2) {
            return Integer.parseInt(digits);
        }
        return Integer.parseInt(digits.substring(digits.length() - 2));
    }

    // Step 3: Submit SQL to webhook
    public void submitFinalQuery(String webhook, String accessToken, String finalSql) {
        webClient.post()
                .uri(webhook)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.set("Authorization", accessToken)) // JWT as-is
                .body(Mono.just(new FinalQueryPayload(finalSql)), FinalQueryPayload.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> System.out.println("Submission response: " + resp))
                .block();
    }
}
