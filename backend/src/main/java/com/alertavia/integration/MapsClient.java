package com.alertavia.integration;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MapsClient {

    private static final Logger log = LoggerFactory.getLogger(MapsClient.class);

    private final RestClient restClient;
    private final String apiKey;

    public MapsClient(@Value("${app.maps.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.create("https://maps.googleapis.com/maps/api");
    }

    public Optional<String> reverseGeocode(double latitude, double longitude) {
        if (apiKey == null || apiKey.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonNode response = restClient.get()
                    .uri("/geocode/json?latlng={lat},{lng}&language=es&key={key}", latitude, longitude, apiKey)
                    .retrieve()
                    .body(JsonNode.class);
            if (response == null || !response.path("results").has(0)) {
                return Optional.empty();
            }
            return Optional.of(response.path("results").get(0).path("formatted_address").asText());
        } catch (RuntimeException ex) {
            log.warn("No se pudo consultar Google Maps: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
