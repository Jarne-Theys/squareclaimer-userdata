package com.square_claimer.user_data.model.Square;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Square {
    private long x, y, user_id, team_id, timestamp;

    public static final long CLAIM_COOLDOWN = 300000L;

    public SimpleSquare simple(){
        return new SimpleSquare(x, y);
    }

    @AllArgsConstructor
    @Getter @Setter
    public static class SimpleSquare{
        private long x, y;
    }
}
