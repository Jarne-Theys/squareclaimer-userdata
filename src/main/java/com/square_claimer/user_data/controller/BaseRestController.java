package com.square_claimer.user_data.controller;

import com.square_claimer.user_data.model.responses.ResponseObject;
import com.square_claimer.user_data.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * More or less a base class for all our RestControllers, we should put error handling in here
 */
@RequestMapping("/api")
public abstract class BaseRestController {

    @Autowired //handles i18n
    private MessageSource messageSource;

    //this will bind to /api/<thing>/ping. Use this as a sanity check
    @GetMapping("/ping")
    public Map<String, Object> ping(){
        Map<String, Object> res = new HashMap<>();
        res.put("response","200");
        res.put("ping","pong");
        return res;
    }


    /**
     * Exception handling, this will return a Map containing what went wrong complete with i18s
     * @param ex the exception
     * @return Map<String, Map/String> containing the error
     * @apiNote API returns response 400 if there's an exception. API will skip i18s if the Accept-Language header is set to debug, which is handy for testing
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ServiceException.class, ResponseStatusException.class})
    public ResponseObject<?> handleValidationExceptions(Exception ex){
        boolean debug = LocaleContextHolder.getLocale().getLanguage().equals("debug");
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException)ex).getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage;
                errorMessage = messageSource.getMessage(error.getDefaultMessage(),null, LocaleContextHolder.getLocale());
                if(debug) errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }
        else if (ex instanceof ServiceException) {
            String msg;
            try{
                msg = messageSource.getMessage(ex.getMessage(),null, LocaleContextHolder.getLocale());
            }catch(Exception e){
                //in case we didn't translate it
                msg = ex.getMessage();
            }
            if(debug) msg = ex.getMessage();
            errors.put(((ServiceException) ex).getAction(), msg);
//            errors.put("status","400");
        }
        else {
            errors.put(((ResponseStatusException)ex).getReason(), ex.getCause().getMessage());
        }
        return ResponseObject.exceptionResponse(errors);
    }

}
