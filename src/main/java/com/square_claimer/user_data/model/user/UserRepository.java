package com.square_claimer.user_data.model.user;

import com.square_claimer.user_data.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(long id);
    User getUserByUserName(String userName);
    User getUserByEmail(String email);
    List<User> getUserByTeam(Team team);
}
