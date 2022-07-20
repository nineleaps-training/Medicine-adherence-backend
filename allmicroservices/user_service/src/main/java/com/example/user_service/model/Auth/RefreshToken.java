package com.example.user_service.model.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("refreshtoken")
public class RefreshToken {

    private String userId;
    private String token;
    private Instant expiryDate;


}
