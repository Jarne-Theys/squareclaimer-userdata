package com.square_claimer.user_data.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.square_claimer.user_data.service.ServiceException;
import lombok.*;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public final class RequestSender{
    @NonNull
    private JsonNodeConverter converter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendPostRequest(String uri, JSONObject json){
        System.out.println("sending POST request to: " + uri);
        try{
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);
            converter.convert(objectMapper.readTree(restTemplate.postForObject(uri, request, String.class)));
        }
        catch (Exception e){
            throw new ServiceException(e, "Exception in sending HTTP request: " + e.getMessage());
        }

    }

    public void sendGetRequest(String uri){
        System.out.println("sending GET request to: " + uri);
        try{
            RestTemplate restTemplate = new RestTemplate();
            converter.convert(objectMapper.readTree(restTemplate.getForObject(uri, String.class)));
        }
        catch (Exception e){
            throw new ServiceException(e, "Exception in sending HTTP request: " + e.getMessage());
        }
    }
}
