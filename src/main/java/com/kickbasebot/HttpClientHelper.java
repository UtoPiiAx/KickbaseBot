package com.kickbasebot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientHelper {

    protected static final String BASE_URL = "https://api.kickbase.com/v4";
    protected static final String BASE_URL_LEAGUES = "https://api.kickbase.com/v4/leagues/";

    protected final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected String token;

    protected JsonNode sendPostRequest(String url, Object requestBody) throws IOException, InterruptedException {
        HttpRequest request = buildPostRequest(url, requestBody);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    protected HttpRequest buildPostRequest(String url, Object requestBody) throws IOException {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();
    }

    protected JsonNode sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = buildGetRequest(url);
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    protected HttpRequest buildGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
    }

    protected JsonNode handleResponse(HttpResponse<String> response) throws IOException {
        if (response.statusCode() == 200) {
            return objectMapper.readTree(response.body());
        }
        throw new RuntimeException("Fehlerhafte Antwort: " + response.statusCode() + " - " + response.body());
    }
}
