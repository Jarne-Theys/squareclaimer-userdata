package com.square_claimer.user_data.model.team;

import com.square_claimer.user_data.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team getTeamById(long id);
    Team getTeamByName(String name);


}
