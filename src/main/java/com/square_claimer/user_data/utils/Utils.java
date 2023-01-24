package com.square_claimer.user_data.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * put stuff we sorta always need somewhere, such as hashing and stuff
 */
public final class Utils {


    //for hashing
    static BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

    private Utils(){
//        enc = new BCryptPasswordEncoder();
    }

    @Bean
    public static PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Hashes the password so we can use it later for authenticating or something
     * @param pass
     * @return
     */
    public static String hash(String pass){
        return enc.encode(pass);
    }

    public static String encode(String s){
        return hash(s);
    }

    /**
     * check if passwords match
     * @param pass password you want to check
     * @param target the encoded password
     * @return true if match
     */
    public static boolean match(String pass, String target){
        return enc.matches(pass,target);
    }

//    public static Utils get(){
//        return this;
//    }

    public static String getWebLink(){
        return "http://" + System.getenv("MORBILE_HOST") + ":6942";
    }

}
