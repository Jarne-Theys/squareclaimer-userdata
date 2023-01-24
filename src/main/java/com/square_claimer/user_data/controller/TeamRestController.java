package com.square_claimer.user_data.controller;

import com.square_claimer.user_data.model.responses.ResponseObject;
import com.square_claimer.user_data.model.team.Team;
import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.service.ServiceException;
import com.square_claimer.user_data.service.TeamService;
import com.square_claimer.user_data.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value={"/api/team"})
public class TeamRestController extends BaseRestController{

    @Autowired
    private TeamService service;

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseObject<List<Team.SimpleTeam>> getAll(){
        return ResponseObject.successResponse(
                "List of all teams",
                service.getAll().stream().map(Team::simple).toList()
        );
    }

    @PostMapping("/join/{id}")
    public ResponseObject<Team.SimpleTeam> addUserToTeam(@PathVariable long id, HttpSession session){
        User user = userService.getUserFromSession(session);
        if(user.getTeam().getId() != 1) throw new ServiceException("You are already on a team");
        return ResponseObject.successResponse(
                "Successfully joined team",
                service.addUserToTeam(
                        user,
                        service.getById(id)
                ).simple()
        );
    }

    @GetMapping("/members")
    public ResponseObject<List<User.VerySimpleUser>> getTeamMembers(HttpSession session){
        return ResponseObject.successResponse(
                "List of team members",
                userService.getUserFromSession(session).getTeam().getMembers().stream().map(User::verySimple).toList()
        );
    }
}
