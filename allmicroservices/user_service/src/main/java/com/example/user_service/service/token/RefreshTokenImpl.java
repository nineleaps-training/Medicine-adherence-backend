package com.example.user_service.service.token;

import com.example.user_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenImpl implements RefreshToken{


    @Autowired
    com.example.user_service.repository.refreshtoken.RefreshToken refreshToken;
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public com.example.user_service.model.Auth.RefreshToken createRefreshToken(String userId) {
        return  refreshToken.save(new com.example.user_service.model.Auth.RefreshToken(userId,jwtUtil.generateToken(userId), Instant.now()));
    }

    @Override
    public com.example.user_service.model.Auth.RefreshToken verifyExpiration(Instant instant) {
        return null;
    }


}
