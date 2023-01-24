package com.square_claimer.user_data.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> getFriendRequestBySenderId(long senderId);
    List<FriendRequest> getFriendRequestByReceiverId(long receiverId);
    FriendRequest findFriendRequestBySenderIdAndReceiverId(long senderId, long receiverId);
}
