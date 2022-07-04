package com.example.user_service.pojos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDetailsDto {

    private String userName;
    private String email;
    private String bio;


}
