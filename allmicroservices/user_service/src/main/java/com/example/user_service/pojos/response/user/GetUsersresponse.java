package com.example.user_service.pojos.response.user;

import com.example.user_service.model.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersresponse {

    List<UserEntity> list;

}
