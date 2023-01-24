package com.square_claimer.user_data.service;

import com.square_claimer.user_data.model.team.Team;
import com.square_claimer.user_data.model.team.TeamRepository;
import com.square_claimer.user_data.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {
    private static final String DEFAULT_TEAM_NAME = "Default";
    private static final String DEFAULT_TEAM_COLOR = "#808080";

    @Autowired
    TeamRepository repository;

    public Team getDefault(){
        try{
            Team defTeam = repository.getTeamByName(DEFAULT_TEAM_NAME);
            if(defTeam == null) throw new IllegalStateException("No default team found");
            return defTeam;
        }
        catch (Exception e){
            return makeDefault();
        }
    }

    private Team makeDefault(){
        Team defTeam = new Team();
        defTeam.setColor(DEFAULT_TEAM_COLOR);
        defTeam.setName(DEFAULT_TEAM_NAME);
        return repository.save(defTeam);
    }

    public List<Team> getAll(){
        try{
            return repository.findAll();
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    public Team addUserToTeam(User user, Team team){
        try{
            Team defaultTeam = getDefault();
            for(Team t : repository.findAll()){
                repository.save(t.removeUser(user, defaultTeam));
            }
            return repository.save(team.addUser(user));
        }
        catch (ServiceException e){
            return team;
        }
    }

    public Team addTeam(Team team){
        try{
            return repository.save(team);
        }
        catch (Exception e){
            throw new ServiceException("Exception creating new team");
        }
    }

    public Team getById(long id) throws ServiceException{
        return repository.getTeamById(id);
    }
}
