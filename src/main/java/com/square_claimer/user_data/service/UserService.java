package com.square_claimer.user_data.service;

import com.square_claimer.user_data.model.team.Team;
import com.square_claimer.user_data.model.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthKeyRepository authKeyRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private TeamService teamService;

    public User addUser(User user){
        if(userRepository.getUserByUserName(user.getUserName()) != null) throw new ServiceException("A user with this name already exists");
        if(userRepository.getUserByEmail(user.getEmail()) != null) throw new ServiceException("A user with this email already exists");
        Team defaultTeam = teamService.getDefault();
        user.setTeam(defaultTeam);
        User newUser = userRepository.save(user);
        teamService.addUserToTeam(newUser, defaultTeam);
        return newUser;
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public User getById(long id) throws ServiceException{
        return userRepository.getUserById(id);
    }

    public User login(String username, String password) throws ServiceException{
        User user = userRepository.getUserByUserName(username);
        if(user == null) throw new ServiceException("Username and/or password is wrong");
        if(user.getStatus() != UserStatus.DEFAULT) throw new ServiceException("User no longer exists");
        if(new BCryptPasswordEncoder().matches(password, user.getPassword())){
            return user;
        }
        else throw new ServiceException("Username and/or password is wrong");
    }

    public AuthKey getAuthKey(String value){
        return authKeyRepository.getByValue(value);
    }

    public AuthKey getAuthKeyForUser(User user){
        try{
            AuthKey authKey =  authKeyRepository.getByUser(user);
            if(authKey == null || authKey.getLifeTime() <= System.currentTimeMillis()) return createNewAuthKey(user);
            else return authKey;
        }
        catch (Exception e){
            return createNewAuthKey(user);
        }
    }

    public AuthKey createNewAuthKey(User user){
        authKeyRepository.deleteByUser(user);
        return authKeyRepository.save(AuthKey.create(user));
    }

    public User getUserFromSession(HttpSession session){
        if(session.getAttribute("userID") == null) throw new ServiceException("user.not.logged.in");
        User user = getById((long)session.getAttribute("userID"));
        if(user == null) throw new ServiceException("user.invalid.id");
        return user;
    }

    public User removeUser(User user){
        if(user.getStatus() == UserStatus.REMOVED) throw new ServiceException("User was already removed");
        else user.setStatus(UserStatus.REMOVED);
        return userRepository.save(user);
    }

    public User restoreUser(User user){
        if(user.getStatus() == UserStatus.DEFAULT) throw new ServiceException("User is not removed or banned");
        else user.setStatus(UserStatus.DEFAULT);
        return userRepository.save(user);
    }

    public FriendRequest sendFriendRequest(User sender, User receiver) throws ServiceException{
        if(!friendRequestRepository.getFriendRequestBySenderId(sender.getId()).stream().filter(
                fr -> fr.getReceiverId() == receiver.getId()
        ).toList().isEmpty())
            throw new ServiceException("You have already sent this user a friend request");
        if(!friendRequestRepository.getFriendRequestByReceiverId(receiver.getId()).stream().filter(
                fr -> fr.getSenderId() == sender.getId()
        ).toList().isEmpty())
            throw new ServiceException("You already have a pending friend request from this user");
        return friendRequestRepository.save(new FriendRequest(sender.getId(), receiver.getId()));
    }

    public User[] acceptFriendRequest(User accepting, User accepted){
        FriendRequest fr = friendRequestRepository.findFriendRequestBySenderIdAndReceiverId(accepted.getId(), accepting.getId());
        if(fr == null) throw new ServiceException("You don't have a friend request from this user");
        friendRequestRepository.delete(fr);
        return addFriends(accepting, accepted);
    }

    public List<FriendRequest.SimpleFriendRequest> getFriendRequests(User user){
        return friendRequestRepository.getFriendRequestByReceiverId(user.getId()).stream().map(
                fr -> new FriendRequest.SimpleFriendRequest(getById(fr.getSenderId()).getUserName(), fr.getSenderId(), fr.getTimeStamp())
        ).toList();
    }

    public User[] addFriends(long uid1, long uid2) throws ServiceException{
        return addFriends(getById(uid1), getById(uid2));
    }

    public User[] addFriends(User u1, User u2) throws ServiceException{
        if(u1 == null || u2 == null) throw new ServiceException("Users not found!");
        if(u1.equals(u2)) throw new ServiceException("Can't add yourself as friend");
        u1.addFriend(u2);
        return new User[]{userRepository.save(u1), userRepository.save(u2)};
    }

    public User[] removeFriends(long uid1, long uid2){
        return removeFriends(getById(uid1), getById(uid2));
    }

    public User[] removeFriends(User u1, User u2){
        if(u1 == null || u2 == null) throw new ServiceException("Users not found!");
        u1.removeFriend(u2);
        return new User[]{userRepository.save(u1), userRepository.save(u2)};
    }

}
