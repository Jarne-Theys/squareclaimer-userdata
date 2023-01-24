package com.square_claimer.user_data.controller;

import com.square_claimer.user_data.model.responses.ResponseObject;
import com.square_claimer.user_data.model.team.Team;
import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping(value={"/api/score"})
public class ScoreRestController extends BaseRestController{
    @Autowired
    private UserService userService;

    @GetMapping("/self")
    public ResponseObject<Map<String, Long>> getUserScore(HttpSession session){
        User user = userService.getUserFromSession(session);

        return ResponseObject.successResponse(
                "User claim score",
                "score",
                new Random().nextLong(10)
        );
    }

    @GetMapping("/friends")
    public ResponseObject<List<UserScore>> getFriendsScore(HttpSession session){
        return ResponseObject.successResponse(
                "Friends claim scores",
                userService.getUserFromSession(session).getFriends().stream().map(
                        u -> new UserScore(
                                u.getId(),
                                u.getUserName(),
                                new Random().nextLong(10)
                        )
                ).toList()
        );
    }

    @GetMapping("/team")
    public ResponseObject<List<UserScore>> getTeamScore(HttpSession session){
        return ResponseObject.successResponse(
                "Friends claim scores",
                userService.getUserFromSession(session).getTeam().getMembers().stream().map(
                        u -> new UserScore(
                                u.getId(),
                                u.getUserName(),
                                new Random().nextLong(10)
                        )
                ).toList()
        );
    }

    private static class UserScore extends User.VerySimpleUser {
        @Getter
        private final long score;
        public UserScore(long id, String userName, long score) {
            super(id, userName);
            this.score = score;
        }
    }
}
