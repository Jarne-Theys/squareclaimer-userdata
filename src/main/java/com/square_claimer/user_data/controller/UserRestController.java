package com.square_claimer.user_data.controller;

import com.square_claimer.user_data.model.responses.ResponseObject;
import com.square_claimer.user_data.model.user.AuthKey;
import com.square_claimer.user_data.model.user.User;
import com.square_claimer.user_data.model.user.UserStatus;
import com.square_claimer.user_data.service.ServiceException;
import com.square_claimer.user_data.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value={"/api/user"})
public class UserRestController extends BaseRestController{

    private static final String USER_ID = "userID";

    @Autowired
    UserService service;

    @GetMapping("/get")
    public ResponseObject<User.SimpleUser> getUserInfo(HttpSession session) throws ServiceException{
        System.out.println(session.getAttribute(USER_ID));
        if(session.getAttribute(USER_ID) == null) throw new ServiceException("You are not logged in, log in te request user info");
        User user = service.getById((long)session.getAttribute(USER_ID));
        if(user == null) throw new ServiceException("Invalid user ID");
        return ResponseObject.successResponse("Logged in user information", user.simple());
    }

    @PostMapping("/add")
    public ResponseObject<User.SimpleUser> add(@RequestBody CreateUser createUser){
        return ResponseObject.successResponse(
                "Created new user",
                service.addUser(User.create(createUser.userName, createUser.email, createUser.password)).simple()
        );
    }

    @PostMapping("/login/credentials")
    public ResponseObject<Map<String, String>> loginByCredentials(@Valid @RequestBody LoginCreds loginCreds, HttpSession session) throws ServiceException {
        System.out.println("logging in user with credentials");
        if(session.getAttribute(USER_ID) == null){
            User user = service.login(loginCreds.username, loginCreds.password);
            session.setAttribute(USER_ID, user.getId());

            return ResponseObject.successResponse(
                    "Successfully logged in",
                    "key",
                    service.getAuthKeyForUser(user).getValue()
            );
        }
        else throw new ServiceException("You are already logged in, log out to log in with a different user");
    }

    @PostMapping("/login/key")
    public ResponseObject<Map<String, String>> loginByAuthKey(@Valid @RequestBody LoginKey loginKey, HttpSession session) throws ServiceException {
        System.out.println("logging in user with key");
        if(session.getAttribute(USER_ID) == null) {
            AuthKey authKey = service.getAuthKey(loginKey.key);
            if (authKey == null) throw new ServiceException("Invalid authentication key");
            User user = authKey.getUser();
            if (user == null) throw new ServiceException("Invalid authentication key");
            if (user.getStatus() != UserStatus.DEFAULT) throw new ServiceException("User no longer exists");
            session.setAttribute(USER_ID, user.getId());

            return ResponseObject.successResponse(
                    "Successfully logged in",
                    "key",
                    authKey.getValue()
            );
        }
        else throw new ServiceException("You are already logged in, log out to log in with different user");
    }

    @GetMapping("/logout")
    public ResponseObject<?> logout(HttpSession session){
        if(session.getAttribute(USER_ID) == null) throw new ServiceException("You are not logged in");
        session.removeAttribute(USER_ID);
        return ResponseObject.successResponse(
                "Logged out successfully",
                null
        );
    }

    @PostMapping("/remove")
    public ResponseObject<User.SimpleUser> RemoveUser(HttpSession session){
        return ResponseObject.successResponse(
                "Removed user",
                service.removeUser(service.getUserFromSession(session)).simple()
        );
    }

    @PostMapping("/friend/send/{id}")
    public ResponseObject<?> sendFriendRequest(@PathVariable long id, HttpSession session){
        User user = service.getUserFromSession(session);
        if(user == null) throw new ServiceException("You need to be logged in to perform this action");
        User receiver = service.getById(id);
        if(receiver == null) throw new ServiceException("Unknown user id");
        service.sendFriendRequest(user, receiver);
        return ResponseObject.successResponse(
                "Successfully send friend request",
                null
        );
    }

    @PostMapping("/friend/accept/{id}")
    public ResponseObject<?> acceptFriendRequest(@PathVariable long id, HttpSession session){
        User user = service.getUserFromSession(session);
        if(user == null) throw new ServiceException("You need to be logged in to perform this action");
        User sender = service.getById(id);
        if(sender == null) throw new ServiceException("Unknown user id");
        service.acceptFriendRequest(user, sender);
        return ResponseObject.successResponse(
                "Successfully accepted friend request",
                user.simple()
        );
    }

    /*
    @GetMapping("/version")
    public String version(){
        System.out.println("version: 0.1");
        return "version: 0.1";
    }*/

    /*
    --------------------------------------------------------------------------------------------
    Request / Response body's
    --------------------------------------------------------------------------------------------
     */

    @Getter @Setter
    private static class CreateUser{
        private String userName, email, password;
    }

    @Getter @Setter
    private static class LoginCreds{
        @NotNull(message = "user.name.missing")
        @NotBlank(message = "user.name.missing")
        private String username;

        @NotNull(message = "user.password.missing")
        @NotBlank(message = "user.password.missing")
        private String password;
    }

    @Getter @Setter
    private static class LoginKey{
        @NotNull(message = "auth.key.missing")
        @NotBlank(message = "auth.key.missing")
        private String key;
    }
}
