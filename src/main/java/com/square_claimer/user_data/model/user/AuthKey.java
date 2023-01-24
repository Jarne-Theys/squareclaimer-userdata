package com.square_claimer.user_data.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class AuthKey {
    private static final long DEFAULT_LIFETIME = 604800000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Getter @Setter
    private String value;

    @OneToOne
    @Getter @Setter
    private User user;

    @Getter @Setter
    private long creationTime;

    @Getter @Setter
    private long lifeTime;

    public AuthKey resetLifeTime(){
        setLifeTime(System.currentTimeMillis() + DEFAULT_LIFETIME);
        return this;
    }

    public static AuthKey create(User user){
        AuthKey authKey = new AuthKey();
        authKey.setUser(user);
        authKey.setCreationTime(System.currentTimeMillis());
        authKey.setLifeTime(System.currentTimeMillis() + DEFAULT_LIFETIME);
        authKey.setValue(UUID.randomUUID().toString());
        return authKey;
    }
}
