package com.example.user_service.service.medicine;

import com.example.user_service.exception.DataAccessExceptionMessage;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.MedicineHistory;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.image.ImagesResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineHistoryResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineResponse;
import com.example.user_service.pojos.response.medicine.UserMedicinesResponse;
import com.example.user_service.pojos.response.sync.SyncResponse;
import com.example.user_service.repository.image.ImageRepository;
import com.example.user_service.repository.medicine.UserMedHistoryRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Messages;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UserMedicineServiceImpl implements UserMedicineService {

    UserRepository userRepository;
    UserMedicineRepository userMedicineRepository;
    ImageRepository imageRepository;
    private ModelMapper mapper;
    UserMedHistoryRepository userMedHistoryRepository;

    Logger logger = LoggerFactory.getLogger(UserMedicineServiceImpl.class);

    private static final String errorMsg = "SQL error!";

    public UserMedicineServiceImpl(UserRepository userRepository, UserMedicineRepository userMedicineRepository, ImageRepository imageRepository, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.userMedicineRepository = userMedicineRepository;
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    @Override
    @Async
    public UserMedicinesResponse getallUserMedicines(String userId) throws UserMedicineException, UserExceptionMessage {

        try {
            UserEntity user = userRepository.getUserById(userId);
            if (user == null) {
                throw new UserExceptionMessage(Messages.USER_NOT_FOUND);
            }
            List<UserMedicines> list = user.getUserMedicines();
            return new UserMedicinesResponse(Messages.SUCCESS, Messages.DATA_FOUND, CompletableFuture.completedFuture(list).get());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(errorMsg + dataAccessException.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public SyncMedicineResponse syncData(String userId, List<UserMedicines> list) throws UserMedicineException {
        try {
            UserEntity user = userRepository.getUserById(userId);
            if (user.getUserMedicines().isEmpty()) {
                throw new UserMedicineException("Unable to sync");
            }
            for (UserMedicines userMedicines : list) {
                userMedicines.setUserEntity(user);
            }
            return new SyncMedicineResponse(Messages.SUCCESS,Messages.SYNCED_MEDICINE);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(errorMsg + dataAccessException.getMessage());
        }
    }


    @Override
    @Async
    public SyncMedicineHistoryResponse syncMedicineHistory(Integer medId, List<MedicineHistoryDTO> medicineHistoryDTOS) throws UserMedicineException {
        try {
            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            if (userMedicines == null) {
                throw new UserMedicineException("Unable to sync");

            }

            List<MedicineHistory> medicineHistories = medicineHistoryDTOS.stream().map(medHid -> {
                MedicineHistory medicineHistory1 = new MedicineHistory();
                medicineHistory1.setHistoryId(medHid.getRemId());
                medicineHistory1.setDate(medHid.getDate());
                medicineHistory1.setTaken(String.join(",", medHid.getTaken()));
                medicineHistory1.setNotTaken(String.join(",", medHid.getNot_taken()));
                medicineHistory1.setUserMedicines(userMedicines);
                return medicineHistory1;
            }).collect(Collectors.toList());
            CompletableFuture.completedFuture(userMedHistoryRepository.saveAll(medicineHistories));

            return new SyncMedicineHistoryResponse(Messages.SUCCESS,Messages.SYNCED_MEDICINE_HISTORY);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(errorMsg + dataAccessException.getMessage());
        }
    }


    @Override
    public MedicineResponse getMedicineHistory(Integer medId) throws UserMedicineException {

        try {
            List<MedicineHistory> medicineHistories = userMedicineRepository.getMedById(medId).getMedicineHistories().subList(0, 2);
            if (medicineHistories.isEmpty()) {
                throw new UserMedicineException(Messages.DATA_NOT_FOUND);
            }
            return new MedicineResponse(Messages.SUCCESS, Messages.DATA_FOUND, medicineHistories);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(errorMsg + dataAccessException.getMessage());
        }
    }

    @Override
    public SyncResponse syncMedicines(String userId, List<MedicinePojo> medicinePojo) throws UserMedicineException {
        try {
            UserEntity userEntity = userRepository.getUserById(userId);

            List<UserMedicines> userMedicinesList = medicinePojo.stream().map(medicinePojo1 -> {
                        UserMedicines userMedicines = new UserMedicines();

                        userMedicines.setMedicineDes(medicinePojo1.getMedicineDes());
                        userMedicines.setMedicineName(medicinePojo1.getMedicineName());
                        userMedicines.setDays(medicinePojo1.getDays());
                        userMedicines.setMedicineId(medicinePojo1.getMedicineId());
                        userMedicines.setEndDate(medicinePojo1.getEndDate());
                        userMedicines.setTitle(medicinePojo1.getTitle());
                        userMedicines.setCurrentCount(medicinePojo1.getCurrentCount());
                        userMedicines.setTotalMedReminders(medicinePojo1.getTotalMedReminders());
                        userMedicines.setStartDate(medicinePojo1.getStartDate());
                        userMedicines.setTime(medicinePojo1.getTime());
                        userMedicines.setUserEntity(userEntity);

                        return userMedicines;
                    })
                    .collect(Collectors.toList());

            userMedicineRepository.saveAll(userMedicinesList);
            return new SyncResponse(Messages.SUCCESS, Messages.SYNC_SUCCESS);
        } catch (Exception exception) {
            throw new UserMedicineException("Sync failed");
        }
    }

    @Override
    public ImagesResponse getUserMedicineImages(Integer medId) {

        try {
            return new ImagesResponse(Messages.SUCCESS, Messages.DATA_FOUND, userMedicineRepository.getMedById(medId)
                    .getImages()
                    .stream()
                    .sorted(Comparator.comparing(Image::getDate).reversed())
                    .collect(Collectors.toList()));

        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(errorMsg + dataAccessException.getMessage());
        }
    }
}
