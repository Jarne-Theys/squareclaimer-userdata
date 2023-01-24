package com.square_claimer.user_data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class UserDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserDataApplication.class, args);
        Locale.setDefault(new Locale("en", "US"));
    }

}
