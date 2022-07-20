package com.example.user_service.service.caretaker;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.exception.JDBCConnectionException;

import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.user_service.exception.caretaker.UserCaretakerException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.caretaker.MyCaretakerDto;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.caretaker.CaretakerListResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.image.SendImageResponse;
import com.example.user_service.pojos.response.patient.PatientRequestResponse;
import com.example.user_service.pojos.response.patient.PatientResponse;
import com.example.user_service.repository.caretaker.UserCaretakerRepository;
import com.example.user_service.repository.image.ImageRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.util.Datehelper;
import com.example.user_service.util.Messages;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CareTakerServiceImpl implements CareTakerService {
    Logger logger = LoggerFactory.getLogger(CareTakerServiceImpl.class);
    private final UserCaretakerRepository userCaretakerRepository;
    private final ModelMapper mapper;
    private final ImageRepository imageRepository;
    private final UserMedicineRepository userMedicineRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public PatientRequestResponse delPatientReq(String cId) {
        logger.info("Delete Patient request : {}", cId);

        try {
            Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(cId);

            if (userCaretaker.isPresent() && (userCaretaker.get().getCId() != null)) {
                userCaretakerRepository.delete(userCaretaker.get());

                return new PatientRequestResponse(Messages.SUCCESS, Messages.DELETED_SUCCESS);
            }

            return new PatientRequestResponse(Messages.FAILED, Messages.ERROR_TRY_AGAIN);
        } catch (Exception e) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE, e.getMessage());

            return new PatientRequestResponse(Messages.FAILED, Messages.DELETED_SUCCESS);
        }
    }

    private UserCaretaker mapToEntity(UserCaretakerDTO userCaretakerDTO) {
        return mapper.map(userCaretakerDTO, UserCaretaker.class);
    }

    @Override
    public CaretakerResponse saveCareTaker(UserCaretakerDTO userCaretakerDTO)  {
        logger.info("Saving caretaker {}", userCaretakerDTO);

        try {
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);

            userCaretaker.setCreatedAt(Datehelper.getcurrentdatatime().toString());

            if (userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId()) != null) {
                throw new UserCaretakerException(Messages.CARETAKER_PRESENT);
            } else {
                userCaretakerRepository.save(userCaretaker);

                return new CaretakerResponse(Messages.SUCCESS,
                        Messages.REQUEST_SENT_SUCCESS,
                        userCaretakerRepository.save(userCaretaker));
            }
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE,
                    dataAccessException.getMessage());

            throw new UserCaretakerException(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    public SendImageResponse sendImageToCaretaker(MultipartFile multipartFile, String filename, String caretakerid,
                                                  String medName, Integer medId)
             {
        logger.info("Send Image to Caretaker with Id : {}", caretakerid);

        try {
            Path path = Paths.get(System.getProperty("user.dir") + "/src/main/upload/static/images",
                    filename.concat(".").concat("jpg"));

            Files.write(path, multipartFile.getBytes());

            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);

            if (userMedicines == null) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }

            String userName = userMedicines.getUserEntity().getUserName();
            Image image = new Image();

            image.setImageUrl(path.getFileName().toString());
            image.setTime(Calendar.getInstance().getTime().toString());
            image.setDate(Calendar.getInstance().getTime());
            image.setUserMedicines(userMedicines);
            image.setCaretakerName(userName);
            imageRepository.save(image);

            String fcmToken =
                    "epkw4MI-RxyMzZjvD6fUl6:APA91bEUyAJpJ5RmDyI1KLcMLJbBPiYSX64oIW4WkNq62zeUlMPUPknGkBHTB_drOBX6CUkiI0Pyfc4Myvt87v6BU69kz0LPq4YM9iWnG9RrNbxIpC4LrtE-zWfNdbB3dbjR2bmogops";

            rabbitTemplate.convertAndSend("project_exchange",
                    "notification_key",
                    new Notificationmessage(fcmToken,
                            "Take medicine",
                            "caretaker",
                            medName,
                            filename + ".jpg"));

            return new SendImageResponse(Messages.SUCCESS, Messages.Image_Sent_Successfully);
        } catch (JDBCConnectionException | IOException e) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE, e.getMessage());
            logger.error("CaretakerService" + " :: " + Messages.SQL_ERROR_MSG);

            throw new UserCaretakerException(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CaretakerResponse updateCaretakerStatus(String cId)  {
        logger.info("Update request status for Id: {}", cId);

        try {
            Optional<UserCaretaker> uc = userCaretakerRepository.findById(cId);

            if ((uc == null) || Objects.isNull(uc.get().getCaretakerUsername())) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }

            uc.get().setReqStatus(true);

            return new CaretakerResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretakerRepository.save(uc.get()));
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE,
                    dataAccessException.getMessage());

            throw new UserCaretakerException(Messages.SQL_ERROR_MSG);
        }
    }

    @Override
    @Cacheable(
            value = "caretakercache",
            key = "#userId"
    )
    public CaretakerListResponse getMyCaretakers(String userId, Integer pageNo, Integer pageSize)
             {
        logger.info("Fetch user Caretakers : {}", userId);

        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<MyCaretakerDto> userCaretaker = userCaretakerRepository.getMyCaretakers(userId, pageable);

            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }

            return new CaretakerListResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE,
                    dataAccessException.getMessage());

            throw new UserCaretakerException(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    public PatientResponse getPatientRequests(String userId, Integer pageNo, Integer pageSize)
             {
        logger.info("Fetch Patients requests for user : {}", userId);

        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<UserCaretaker> userCaretaker = userCaretakerRepository.getPatientRequests(userId, pageable);

            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }

            return new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE,
                    dataAccessException.getMessage());

            throw new UserCaretakerException(Messages.ERROR_TRY_AGAIN);
        }
    }

    @Override
    public PatientResponse getPatientsUnderMe(String userId, Integer pageNo, Integer pageSize)
            {
        logger.info("Fetch patients for user : {} ", userId);

        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<UserCaretaker> userCaretaker = userCaretakerRepository.getPatientsUnderMe(userId, pageable);

            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }

            return new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            com.example.user_service.config.log.Logger.errorLog(Messages.CARETAKER_SERVICE,
                    dataAccessException.getMessage());

            throw new UserCaretakerException(Messages.ERROR_TRY_AGAIN);
        }
    }
}
