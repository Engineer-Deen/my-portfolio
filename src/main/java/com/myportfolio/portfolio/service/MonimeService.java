package com.myportfolio.portfolio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MonimeService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${monime.access-token}")
    private String accessToken;

    @Value("${monime.space-id}")
    private String spaceId;

    @Value("${site.base-url}")
    private String siteBaseUrl;

    public String createCheckoutSession(int amountLeones) throws Exception {
        if (amountLeones < 1) {
            throw new IllegalArgumentException("Please enter an amount of at least SLE 1.");
        }

        Map<String, Object> lineItem = Map.of(
                "type", "custom",
                "name", "Support Abdul Deen Kamara's Work",
                "price", Map.of(
                        "currency", "SLE",
                        "value", amountLeones * 100
                ),
                "quantity", 1
        );

        Map<String, Object> payload = Map.of(
                "name", "Support Payment",
                "lineItems", List.of(lineItem),
                "successUrl", siteBaseUrl + "/?payment=success#support",
                "cancelUrl", siteBaseUrl + "/?payment=cancelled#support"
        );

        String jsonPayload = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.monime.io/v1/checkout-sessions"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .header("Monime-Space-Id", spaceId)
                .header("Monime-Version", "caph.2025-08-23")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException("Monime API error (" + response.statusCode() + "): " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        return root.path("result").path("redirectUrl").asText();
    }
}