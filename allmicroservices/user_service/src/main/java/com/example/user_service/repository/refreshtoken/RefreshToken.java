package com.example.user_service.repository.refreshtoken;

import org.springframework.data.repository.CrudRepository;

public interface RefreshToken extends CrudRepository<com.example.user_service.model.Auth.RefreshToken,Long> {
}
