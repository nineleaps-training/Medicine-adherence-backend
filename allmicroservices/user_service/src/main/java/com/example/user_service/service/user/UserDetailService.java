package com.example.user_service.service.user;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.pojos.response.user.UserUpdateDetailResponse;


public interface UserDetailService {

    public UserUpdateDetailResponse saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws  UserExceptionMessage;
}
