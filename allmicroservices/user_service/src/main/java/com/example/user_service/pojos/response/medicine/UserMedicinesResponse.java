package com.example.user_service.pojos.response.medicine;

import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.response.sync.SyncResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMedicinesResponse implements Serializable {

    private String status;
    private String message;
    private List<UserMedicines> userMedicinesList;

}
