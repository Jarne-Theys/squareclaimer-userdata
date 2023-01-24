package com.square_claimer.user_data.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthKeyRepository extends JpaRepository<AuthKey, Long> {
    AuthKey getById(long id);
    AuthKey getByUser(User user);
    AuthKey getByValue(String value);
    AuthKey deleteById(long id);
    void deleteByUser(User user);
}
