package com.example.user_service.pojos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityDTO {

    @NotNull(message = "UserName cannot be null")
    @NotBlank(message = "UserName cannot be Empty")
    private String userName;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be Empty")
    private String email;

}
