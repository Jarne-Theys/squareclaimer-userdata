package com.square_claimer.user_data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.square_claimer.user_data.model.Square.Square;
import com.square_claimer.user_data.model.responses.ResponseObject;
import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.service.ServiceException;
import com.square_claimer.user_data.service.SquareService;
import com.square_claimer.user_data.service.UserService;
import com.square_claimer.user_data.utils.RequestSender;
import com.square_claimer.user_data.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping(value={"/api/square"})
public class SquareRestController extends BaseRestController{
    @Autowired
    private UserService userService;

    @Autowired
    private SquareService service;

    @PostMapping("/claim/{x}/{y}")
    public ResponseObject<List<Square.SimpleSquare>> claimSquare(@PathVariable long x, @PathVariable long y, HttpSession session) {
        return ResponseObject.successResponse(
                "Successfully claimed square(s)",
                service.claimSquare(x, y, userService.getUserFromSession(session))
        );
    }

    @GetMapping("/radius/{x}/{y}/{r}")
    public ResponseObject<List<Square>> getSquaresInRadius(@PathVariable long x, @PathVariable long y, @PathVariable long r){
        return ResponseObject.successResponse(
                "Successfully requested squares",
                service.getSquaresInRadius(x, y, r)
        );
    }

    @GetMapping("/can/{x}/{y}")
    public ResponseObject<?> canClaimSquare(@PathVariable long x, @PathVariable long y, HttpSession session){
        return ResponseObject.successResponse(
                "Successfully asked if can claim square",
                "claimable",
                service.canClaim(x, y, userService.getUserFromSession(session))
        );
    }
}
