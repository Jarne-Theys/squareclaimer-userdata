package com.square_claimer.user_data.model.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ResponseObject<T> {
    private boolean success;
    private String message;
    private T body;
    Map<String, String> errors;

    private ResponseObject(boolean success, String message, T body, Map<String, String> errors){
        this.success = success;
        this.message = message;
        this.body = body;
        this.errors = errors;
    }

    public static ResponseObject<?> exceptionResponse(Map<String, String> errors){
        return new ResponseObject<>(false, "An exception occurred", null, errors);
    }

    public static <T> ResponseObject<T> successResponse(String message, T body){
        return new ResponseObject<>(true, message, body, null);
    }

    public static <T> ResponseObject<Map<String, T>> successResponse(String message, String key, T value){
        Map<String, T> body = new HashMap<>();
        body.put(key, value);
        return successResponse(message, body);
    }
}
