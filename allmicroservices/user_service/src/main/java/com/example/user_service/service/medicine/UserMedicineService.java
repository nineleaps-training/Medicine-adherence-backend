package com.example.user_service.service.medicine;

import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.image.ImagesResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineHistoryResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineResponse;
import com.example.user_service.pojos.response.medicine.UserMedicinesResponse;
import com.example.user_service.pojos.response.sync.SyncResponse;
import java.util.List;
public interface UserMedicineService {


    UserMedicinesResponse getallUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage, InterruptedException;

    SyncMedicineResponse syncData(String userId , List<UserMedicines> list) throws UserMedicineException;

    SyncMedicineHistoryResponse syncMedicineHistory(Integer medId , List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException;

    MedicineResponse getMedicineHistory(Integer medId) throws UserMedicineException;
    SyncResponse syncMedicines(String userId, List<MedicinePojo> medicinePojo) throws UserMedicineException;

    ImagesResponse getUserMedicineImages(Integer medId) throws UserMedicineException;

}
