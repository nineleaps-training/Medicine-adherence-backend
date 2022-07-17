package com.example.user_service.pojos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMailDto implements Serializable {

    private String userName;
    private String email;
    private String picPath;

}
