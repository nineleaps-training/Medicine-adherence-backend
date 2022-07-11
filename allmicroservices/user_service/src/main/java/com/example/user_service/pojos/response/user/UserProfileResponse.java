package com.example.user_service.pojos.response.user;

import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

     private String status;
     private List<UserEntity> userEntityList;
     private List<UserMedicines> medicinesList;

}
///