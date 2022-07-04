package com.example.user_service.pojos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String bio;
    private int age;
    private Long userContact;
    private String gender;
    private String bloodGroup;
    private String martialStatus;
    private int weight;
}
//