package com.example.user_service.service.medicine;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.response.image.ImagesResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineHistoryResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineResponse;
import com.example.user_service.pojos.response.medicine.UserMedicinesResponse;
import com.example.user_service.repository.image.ImageRepository;
import com.example.user_service.repository.medicine.UserMedHistoryRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Messages;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserMedicineServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMedicineRepository userMedicineRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    UserMedHistoryRepository userMedHistoryRepository;
    UserMedicineServiceImpl userMedicineService;
    @BeforeEach
    void init(){
        userMedicineService
                 = new UserMedicineServiceImpl(userRepository,userMedicineRepository,imageRepository,userMedHistoryRepository);
    }

    @Test
    void getallUserMedicinesStatus() throws UserExceptionMessage, UserMedicineException {

        UserEntity userEntityTest = new UserEntity("erer4","Nikunj","nikkubisht12@gmail.com", LocalDateTime.now(),LocalDateTime.now(),null,null);
        when(userRepository.getUserById("1234")).thenReturn(userEntityTest);
        UserMedicinesResponse userMedicinesResponse = userMedicineService.getallUserMedicines("1234");
        Assertions.assertEquals(Messages.SUCCESS,userMedicinesResponse.getStatus());
    }

    @Test
    void getallUserMedicinesMessage() throws UserExceptionMessage, UserMedicineException {

        UserEntity userEntityTest = new UserEntity("erer4","Nikunj","nikkubisht12@gmail.com", LocalDateTime.now(),LocalDateTime.now(),null,null);
        when(userRepository.getUserById("1234")).thenReturn(userEntityTest);
        UserMedicinesResponse userMedicinesResponse = userMedicineService.getallUserMedicines("1234");
        Assertions.assertEquals(Messages.DATA_FOUND,userMedicinesResponse.getMessage());
    }

    @Test
    void getallUserMedicinesException() throws UserExceptionMessage, UserMedicineException {
        when(userRepository.getUserById("1234")).thenReturn(null);
       try{
           UserMedicinesResponse userMedicinesResponse = userMedicineService.getallUserMedicines("1234");

       }catch (UserExceptionMessage userExceptionMessage){
        Assertions.assertEquals(Messages.USER_NOT_FOUND, userExceptionMessage.getMessage());
    }}

    @Test
    void getallUserMedicinesSQLException() throws UserExceptionMessage {
        when(userRepository.getUserById("1234")).thenThrow(JDBCConnectionException.class);
        try{
            UserMedicinesResponse userMedicinesResponse = userMedicineService.getallUserMedicines("1234");
        }catch (UserMedicineException userExceptionMessage){
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, userExceptionMessage.getMessage());
        }}


    @Test
    void syncDataStatus() throws UserMedicineException {
        UserEntity userEntityTest = new UserEntity("erer4","Nikunj","nikkubisht12@gmail.com", LocalDateTime.now(),LocalDateTime.now(),null,null);
        List<UserMedicines> userMedicinesList = new ArrayList<>(Arrays.asList(new UserMedicines(1234,null,"Alli","Eat 200mg","Fri",null,"10:30","200mg",12,4,null,null,null)));
        when(userRepository.getUserById("1234")).thenReturn(userEntityTest);
        when(userMedicineRepository.saveAll(userMedicinesList)).thenReturn(userMedicinesList); ;
        SyncMedicineResponse syncMedicineResponse = userMedicineService.syncData("1234",userMedicinesList);
        Assertions.assertEquals(Messages.SUCCESS,syncMedicineResponse.getStatus());
    }

    @Test
    void syncDataMessage() throws UserMedicineException {
        UserEntity userEntityTest = new UserEntity("erer4","Nikunj","nikkubisht12@gmail.com", LocalDateTime.now(),LocalDateTime.now(),null,null);
        List<UserMedicines> userMedicinesList = new ArrayList<>(Arrays.asList(new UserMedicines(1234,null,"Alli","Eat 200mg","Fri",null,"10:30","200mg",12,4,null,null,null)));
        when(userRepository.getUserById("1234")).thenReturn(userEntityTest);
        when(userMedicineRepository.saveAll(userMedicinesList)).thenReturn(userMedicinesList); ;
        SyncMedicineResponse syncMedicineResponse = userMedicineService.syncData("1234",userMedicinesList);
        Assertions.assertEquals(Messages.SYNCED_MEDICINE,syncMedicineResponse.getMessage());
    }

    @Test
    void syncDataException() {
        List<UserMedicines> userMedicinesList = new ArrayList<>(Arrays.asList(new UserMedicines(1234,null,"Alli","Eat 200mg","Fri",null,"10:30","200mg",12,4,null,null,null)));
        when(userRepository.getUserById("1234")).thenThrow(JDBCConnectionException.class);
        try{
            SyncMedicineResponse syncMedicineResponse = userMedicineService.syncData("1234",userMedicinesList);
        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN,userMedicineException.getMessage());

        }
    }

    @Test
    void syncDataUserException() {
        List<UserMedicines> userMedicinesList = new ArrayList<>(Arrays.asList(new UserMedicines(1234,null,"Alli","Eat 200mg","Fri",null,"10:30","200mg",12,4,null,null,null)));
        when(userRepository.getUserById("1234")).thenReturn(null);
        try{
            SyncMedicineResponse syncMedicineResponse = userMedicineService.syncData("1234",userMedicinesList);
        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals(Messages.USER_NOT_FOUND,userMedicineException.getMessage());

        }
    }

    @Test
    void syncMedicineHistory() throws UserMedicineException {

        UserMedicines userMedicines
                 = new UserMedicines(1234,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,null);

        List<MedicineHistoryDTO> medicineHistoryDTOList = new ArrayList<>(Arrays.asList(new MedicineHistoryDTO(123,null,new String[]{"10:30"},new String[]{"12:23"})));
        when(userMedicineRepository.getMedById(1234)).thenReturn(userMedicines);
        SyncMedicineHistoryResponse syncMedicineHistoryResponse = userMedicineService.syncMedicineHistory(1234,medicineHistoryDTOList);
        Assertions.assertEquals(Messages.SUCCESS,syncMedicineHistoryResponse.getStatus());
    }
    @Test
    void syncMedicineHistorySqlException() throws UserMedicineException {

        UserMedicines userMedicines
                = new UserMedicines(1234,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,null);

        List<MedicineHistoryDTO> medicineHistoryDTOList = new ArrayList<>(Arrays.asList(new MedicineHistoryDTO(123,null,new String[]{"10:30"},new String[]{"12:23"})));
        when(userMedicineRepository.getMedById(1234)).thenThrow(JDBCConnectionException.class);
        try {
            SyncMedicineHistoryResponse syncMedicineHistoryResponse = userMedicineService.syncMedicineHistory(1234,medicineHistoryDTOList);

        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN,userMedicineException.getMessage());

        }
    }
    @Test
    void syncMedicineHistoryemptyException() throws UserMedicineException {


        List<MedicineHistoryDTO> medicineHistoryDTOList = new ArrayList<>(Arrays.asList(new MedicineHistoryDTO(123,null,new String[]{"10:30"},new String[]{"12:23"})));
        when(userMedicineRepository.getMedById(1234)).thenReturn(null);
        try {
            SyncMedicineHistoryResponse syncMedicineHistoryResponse = userMedicineService.syncMedicineHistory(1234,medicineHistoryDTOList);

        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals(Messages.UNABLE_TO_SYNC,userMedicineException.getMessage());

        }
    }
    @Test
    void getMedicineHistoryStatus() throws UserMedicineException {
        List<MedicineHistory> medicineHistoryList = new ArrayList<>(Arrays.asList(new MedicineHistory(1234,null,"10:30","11:00",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,medicineHistoryList,null);

        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        MedicineResponse medicineResponse = userMedicineService.getMedicineHistory(123);
        Assertions.assertEquals(Messages.SUCCESS,medicineResponse.getStatus());

    }

    @Test
    void getMedicineHistoryMessage() throws UserMedicineException {
        List<MedicineHistory> medicineHistoryList = new ArrayList<>(Arrays.asList(new MedicineHistory(1234,null,"10:30","11:00",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,medicineHistoryList,null);

        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        MedicineResponse medicineResponse = userMedicineService.getMedicineHistory(123);
        Assertions.assertEquals(Messages.DATA_FOUND,medicineResponse.getMessage());

    }

    @Test
    void getMedicineHistoryException() throws UserMedicineException {
        List<MedicineHistory> medicineHistoryList = new ArrayList<>(Arrays.asList());
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,medicineHistoryList,null);

        when(userMedicineRepository.getMedById(123)).thenReturn(
                userMedicines
        );
        try {
            MedicineResponse medicineResponse = userMedicineService.getMedicineHistory(123);

        }catch (UserMedicineException userMedicineException){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userMedicineException.getMessage());

        }

    }

    @Test
    void syncMedicines() {
    }

    @Test
    void getUserMedicineImagesStatus() throws UserMedicineException {

        List<Image> images = new ArrayList<>(Arrays.asList(new Image("123",new Date(),"10:30","Nikunj","32rer",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,images);

        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImagesResponse imagesResponse = userMedicineService.getUserMedicineImages(123);
        Assertions
                .assertEquals(Messages.SUCCESS
                        ,imagesResponse.getStatus());
    }
    @Test
    void getUserMedicineImagesMessage() throws UserMedicineException {

        List<Image> images = new ArrayList<>(Arrays.asList(new Image("123",new Date(),"10:30","Nikunj","32rer",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,images);

        when(userMedicineRepository.getMedById(123)).thenReturn(userMedicines);
        ImagesResponse imagesResponse = userMedicineService.getUserMedicineImages(123);
        Assertions
                .assertEquals(Messages.DATA_FOUND
                        ,imagesResponse.getMessage());
    }
    @Test
    void getUserMedicineImagesSQLException() throws UserMedicineException {

        List<Image> images = new ArrayList<>(Arrays.asList(new Image("123",new Date(),"10:30","Nikunj","32rer",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,images);

        when(userMedicineRepository.getMedById(123)).thenThrow(JDBCConnectionException.class);
       try {
           ImagesResponse imagesResponse = userMedicineService.getUserMedicineImages(123);

       }catch (UserMedicineException userMedicineException){
           Assertions
                   .assertEquals(Messages.ERROR_TRY_AGAIN
                           ,userMedicineException.getMessage());

       }
    }
    @Test
    void getUserMedicineImagesEmptyException() throws UserMedicineException {

        List<Image> images = new ArrayList<>(Arrays.asList(new Image("123",new Date(),"10:30","Nikunj","32rer",null)));
        UserMedicines userMedicines
                = new UserMedicines(123,null,"Amif","200mg","FRI",null,"10:30","Eat 300mg",12,3,null,null,images);

        when(userMedicineRepository.getMedById(123)).thenReturn(null);
        try {
            ImagesResponse imagesResponse = userMedicineService.getUserMedicineImages(123);

        }catch (UserMedicineException userMedicineException){
            Assertions
                    .assertEquals(Messages.DATA_NOT_FOUND
                            ,userMedicineException.getMessage());

        }
    }

}