package com.square_claimer.user_data.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long timeStamp;
    private long senderId;
    private long receiverId;

    public FriendRequest(long senderId, long receiverId){
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timeStamp = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendRequest that)) return false;
        return id == that.id && timeStamp == that.timeStamp && senderId == that.senderId && receiverId == that.receiverId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timeStamp, senderId, receiverId);
    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleFriendRequest{
        private String userName;
        private long userId;
        private long timestamp;
    }
}
