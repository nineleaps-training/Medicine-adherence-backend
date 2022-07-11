package com.example.user_service.pojos.response.user;


import com.example.user_service.model.user.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDetailResponse {

    private String status;
    private String message;
    private UserDetails userDetails;

}
