package com.example.user_service.pojos.response.user;

import com.example.user_service.pojos.dto.user.UserMailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMailResponse {

    private String status;
    private String message;
    private UserMailDto userMailDto;

}
