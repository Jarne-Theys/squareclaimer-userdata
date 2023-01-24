package com.square_claimer.user_data.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.square_claimer.user_data.model.team.Team;
import com.square_claimer.user_data.service.ServiceException;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "morbileUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @NotBlank(message = "user.name.missing")
    @NotNull(message = "user.name.missing")
    @Getter @Setter
    private String userName;

    @NotBlank(message = "user.password.missing")
    @NotNull(message = "user.password.missing")
    @Getter
    private String password;

    @NotBlank(message = "user.email.missing")
    @NotNull(message = "user.name.missing")
    @Email(message = "user.email.invalid")
    @Getter @Setter
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Getter @Setter
    private UserStatus status = UserStatus.DEFAULT;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "team_id")
    @Getter @Setter
    private Team team;

    @ManyToMany
    @JsonBackReference
    @Getter @Setter
    private List<User> friends = new ArrayList<>();

    public void setPassword(String pass){
        this.password = new BCryptPasswordEncoder().encode(pass);
    }

    public void addFriend(User user){
        this.friends.add(user);
        user.getFriends().add(this);
    }

    public void removeFriend(User user){
        this.friends.remove(user);
        user.getFriends().remove(this);
    }

    public SimpleUser simple(){
        return new SimpleUser(
                this.id,
                this.userName,
                this.team.getName(),
                this.team.getId(),
                this.friends.stream().map(User::verySimple).toList()
        );
    }

    public VerySimpleUser verySimple(){
        return new VerySimpleUser(this.id, this.userName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(team, user.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, email, team);
    }

    public static User create(String userName, String email, String password){
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Getter @Setter
    public static class SimpleUser extends VerySimpleUser{
        private String teamName;
        private long teamId;
        private List<VerySimpleUser> friends;

        public SimpleUser(long id, String userName, String teamName, long teamId, List<VerySimpleUser> friends) {
            super(id, userName);
            this.teamName = teamName;
            this.teamId = teamId;
            this.friends = friends;
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class VerySimpleUser{
        private long id;
        private String userName;
    }
}
