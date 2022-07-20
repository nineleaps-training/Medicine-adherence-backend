package com.example.user_service.pojos.response.sync;

import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.response.medicine.UserMedicinesResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse implements Serializable
{

    private String status;
    private String message;
    private List<UserMedicines> medicines;

}
