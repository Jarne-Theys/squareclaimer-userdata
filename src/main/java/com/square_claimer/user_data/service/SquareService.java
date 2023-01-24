package com.square_claimer.user_data.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.square_claimer.user_data.controller.SquareRestController;
import com.square_claimer.user_data.model.Square.Square;
import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.utils.RequestSender;
import com.square_claimer.user_data.utils.Utils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SquareService {

    @Autowired
    private UserService userService;

    public List<Square.SimpleSquare> claimSquare(long x, long y, User user) throws ServiceException{
        List<Square.SimpleSquare> squares = new ArrayList<>();
        new RequestSender(
                node -> {
                    for(int i = 0 ; i < node.size() ; i++){
                        squares.add(new Square.SimpleSquare(node.get(i).get(0).asInt(), node.get(i).get(1).asInt()));
                    }
                }
        ).sendPostRequest(
                Utils.getWebLink() + "/claim/" + x + "/" + y + "/" + user.getTeam().getId() + "/" + user.getId(),
                new JSONObject()
        );
        if(squares.isEmpty()) throw new ServiceException("Could not claim this square");
        return squares;
    }

    public List<Square> getSquaresInRadius(long x, long y, long r) throws ServiceException{
        if(r < 0) throw new ServiceException("Radius must be positive");
        List<Square> squares = new ArrayList<>();
        new RequestSender(
                node -> {
                    long b = 2 * r + 1;
                    for(int i = 0; i < node.size() ; i++){
                        squares.add(new Square(
                                i % b + (x - r),
                                i / b + (y - r),
                                node.get(i).get("user").asLong(),
                                node.get(i).get("team").asLong(),
                                node.get(i).get("timestamp").asLong()
                        ));
                    }
                }
        ).sendGetRequest(
                Utils.getWebLink() + "/rect_rad/" + x + "/" + y + "/" + r
        );
        return squares;
    }

    public boolean canClaim(long x, long y, User user){
        AtomicBoolean claimable = new AtomicBoolean(false);
        new RequestSender(
                node -> {
                    JsonNode square = node.get(0);
                    User claimedBy = userService.getById(square.get("user").asLong());
                    //check claimedBy exists
                    if(claimedBy == null) {
                        claimable.set(true);
                    }
                    //check already owned
                    else if(claimedBy.equals(user)){
                        claimable.set(user.getTeam().getId() != square.get("team").asLong());
                    }
                    //check if different users are in the same team
                    else if(Objects.equals(claimedBy.getTeam().getId(), user.getTeam().getId())){
                        claimable.set(false);
                    }
                    //check for cooldown time
                    else claimable.set(
                        square.get("timestamp").asLong() + Square.CLAIM_COOLDOWN <= System.currentTimeMillis()
                    );
                }
        ).sendGetRequest(
                Utils.getWebLink() + "/rect_rad/" + x + "/" + y + "/" + 0
        );
        return claimable.get();
    }
}
