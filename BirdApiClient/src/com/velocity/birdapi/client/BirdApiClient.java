package com.velocity.birdapi.client;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.velocity.itest.avian.dto.BirdDto;
import com.velocity.itest.avian.dto.SightingDto;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service client for interacting with the Bird REST API.
 */
public class BirdApiClient {

    private static final String API_BASE_URL = "http://localhost:8080/api/v1";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BirdApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        this.objectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Register a custom serializer and deserializer for LocalDateTime
        SimpleModule module = new SimpleModule();
        
        // Custom Serializer (Object -> JSON)
        module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString());
            }
        });
        
        // Custom Deserializer (JSON -> Object)
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return LocalDateTime.parse(p.getText());
            }
        });
        
        objectMapper.registerModule(module);
    }

    // --- Bird Endpoints ---

    /**
     * Adds a new bird to the database.
     * @param bird The bird data to add.
     * @return The created BirdDto object.
     * @throws Exception If the API call fails.
     */
    public BirdDto addBird(BirdDto bird) throws Exception {
        String json = objectMapper.writeValueAsString(bird);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(API_BASE_URL + "/birds"))
                .header("Content-Type", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Failed to add bird: " + response.body());
        }
        return objectMapper.readValue(response.body(), BirdDto.class);
    }
    
    /**
     * Fetches all birds from the API.
     * @return A list of BirdDto objects.
     * @throws Exception If the API call fails.
     */
    public List<BirdDto> getAllBirds() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_BASE_URL + "/birds"))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch birds: " + response.body());
        }
        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, BirdDto.class));
    }

    /**
     * Fetches a bird by its ID.
     * @param id The ID of the bird to fetch.
     * @return The BirdDto object.
     * @throws Exception If the API call fails or bird not found.
     */
    public BirdDto getBirdById(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_BASE_URL + "/birds/" + id))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch bird: " + response.body());
        }
        return objectMapper.readValue(response.body(), BirdDto.class);
    }
    
    /**
     * Updates an existing bird.
     * @param id The ID of the bird to update.
     * @param bird The updated bird data.
     * @return The updated BirdDto object.
     * @throws Exception If the API call fails.
     */
    public BirdDto updateBird(Long id, BirdDto bird) throws Exception {
        String json = objectMapper.writeValueAsString(bird);
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(API_BASE_URL + "/birds/" + id))
                .header("Content-Type", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to update bird: " + response.body());
        }
        return objectMapper.readValue(response.body(), BirdDto.class);
    }

    /**
     * Deletes a bird by its ID.
     * @param id The ID of the bird to delete.
     * @throws Exception If the API call fails.
     */
    public void deleteBird(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(API_BASE_URL + "/birds/" + id))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) { // 204 No Content is the expected successful response
            throw new RuntimeException("Failed to delete bird: " + response.body());
        }
    }
    
    /**
     * Queries birds by name and color.
     * @param name The name of the bird.
     * @param color The color of the bird.
     * @return A list of matching BirdDto objects.
     * @throws Exception If the API call fails.
     */
    public List<BirdDto> queryBirds(String name, String color) throws Exception {
        String uri = API_BASE_URL + "/birds/query?";
        
        if (name != null && !name.isEmpty()) {
            uri += "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8);
        }
        if (color != null && !color.isEmpty()) {
            if (name != null && !name.isEmpty()) uri += "&";
            uri += "color=" + URLEncoder.encode(color, StandardCharsets.UTF_8);
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to query birds: " + response.body());
        }
        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, BirdDto.class));
    }
    
    // --- Sighting Endpoints ---
    
    /**
     * Adds a new sighting to the database.
     * @param sighting The sighting data to add.
     * @return The created SightingDto object.
     * @throws Exception If the API call fails.
     */
    public SightingDto addSighting(SightingDto sighting) throws Exception {
        String json = objectMapper.writeValueAsString(sighting);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(API_BASE_URL + "/sightings"))
                .header("Content-Type", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Failed to add sighting: " + response.body());
        }
        return objectMapper.readValue(response.body(), SightingDto.class);
    }
    
    /**
     * Fetches all sightings from the API.
     * @return A list of SightingDto objects.
     * @throws Exception If the API call fails.
     */
    public List<SightingDto> getAllSightings() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_BASE_URL + "/sightings"))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch sightings: " + response.body());
        }
        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, SightingDto.class));
    }
    
    /**
     * Deletes a sighting by its ID.
     * @param id The ID of the sighting to delete.
     * @throws Exception If the API call fails.
     */
    public void deleteSighting(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(API_BASE_URL + "/sightings/" + id))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) { // 204 No Content is the expected successful response
            throw new RuntimeException("Failed to delete sighting: " + response.body());
        }
    }
    
    /**
     * Queries sightings by various optional criteria.
     * @param location The location of the sighting (optional).
     * @param birdId The ID of the bird (optional).
     * @param startDate The start of the time interval (optional).
     * @param endDate The end of the time interval (optional).
     * @return A list of matching SightingDto objects.
     * @throws Exception If the API call fails.
     */
    public List<SightingDto> querySightings(String location, Long birdId, String startDate, String endDate) throws Exception {
        StringBuilder uriBuilder = new StringBuilder(API_BASE_URL + "/sightings/query?");
        
        List<String> queryParams = new java.util.ArrayList<>();
        if (location != null && !location.isEmpty()) {
            queryParams.add("location=" + URLEncoder.encode(location, StandardCharsets.UTF_8));
        }
        if (birdId != null) {
            queryParams.add("birdId=" + birdId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryParams.add("startDate=" + URLEncoder.encode(startDate, StandardCharsets.UTF_8));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryParams.add("endDate=" + URLEncoder.encode(endDate, StandardCharsets.UTF_8));
        }
        
        uriBuilder.append(queryParams.stream().collect(Collectors.joining("&")));
        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uriBuilder.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to query sightings: " + response.body());
        }
        return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, SightingDto.class));
    }
}