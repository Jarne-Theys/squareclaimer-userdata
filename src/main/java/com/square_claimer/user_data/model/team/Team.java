package com.square_claimer.user_data.model.team;

import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.service.ServiceException;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @NotBlank(message = "team.name.missing")
    @Getter @Setter
    private String name;

    @NotBlank(message = "team.color.missing")
    @Getter @Setter
    private String color;

    @OneToMany(cascade = CascadeType.PERSIST)
    @Getter @Setter
    private List<User> members = new ArrayList<>();

    public Team addUser(User user) throws ServiceException{
        if(this.members.contains(user)) throw new ServiceException("Team already contains user");
        this.members.add(user);
        user.setTeam(this);
        return this;
    }

    public Team removeUser(User user, Team defaultTeam){
        this.members.remove(user);
        user.setTeam(defaultTeam);
        return this;
    }

    public SimpleTeam simple(){
        return new SimpleTeam(id, members.size(), name, color);
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleTeam{
        private long id, members;
        private String name, color;
    }
}
