package com.example.user_service.pojos.response.medicine;

import com.example.user_service.model.medicine.UserMedicines;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMedicinesResponse {

    private String status;
    private String message;
    private List<UserMedicines> userMedicinesList;

}
