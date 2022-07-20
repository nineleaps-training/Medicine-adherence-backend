package com.example.user_service.service.token;

import java.time.Instant;

public interface RefreshToken {

   com.example.user_service.model.Auth.RefreshToken createRefreshToken(String userId);
   com.example.user_service.model.Auth.RefreshToken verifyExpiration(Instant instant);

}
