package com.example.user_service.service.caretaker;


import com.example.user_service.exception.DataAccessExceptionMessage;
import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.model.image.Image;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.caretaker.MyCaretakerDto;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.caretaker.CaretakerListResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.image.SendImageResponse;
import com.example.user_service.pojos.response.patient.PatientRequestResponse;
import com.example.user_service.pojos.response.patient.PatientResponse;
import com.example.user_service.repository.image.ImageRepository;
import com.example.user_service.repository.caretaker.UserCaretakerRepository;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.util.Datehelper;
import com.example.user_service.util.Messages;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class CareTakerServiceImpl implements CareTakerService {

    private final UserCaretakerRepository userCaretakerRepository;
    private final ModelMapper mapper;
    private final ImageRepository imageRepository;
    private final UserMedicineRepository userMedicineRepository;
    private final RabbitTemplate rabbitTemplate;

    Logger logger = LoggerFactory.getLogger(CareTakerServiceImpl.class);

    CareTakerServiceImpl(ImageRepository imageRepository, UserMedicineRepository userMedicineRepository, RabbitTemplate rabbitTemplate, UserCaretakerRepository userCaretakerRepository, ModelMapper modelMapper) {
        this.imageRepository = imageRepository;
        this.userMedicineRepository = userMedicineRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.userCaretakerRepository = userCaretakerRepository;
        this.mapper = modelMapper;
    }

    @Override
    public CaretakerResponse saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException {

        try {
            UserCaretaker userCaretaker = mapToEntity(userCaretakerDTO);
            userCaretaker.setCreatedAt(Datehelper.getcurrentdatatime());
            if (userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId()) != null) {
                throw new UserCaretakerException(Messages.CARETAKER_PRESENT);
            } else {
                return new CaretakerResponse(Messages.SUCCESS, Messages.REQUEST_SENT_SUCCESS, userCaretakerRepository.save(userCaretaker));
            }
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public CaretakerResponse updateCaretakerStatus(String cId) throws UserCaretakerException {
        try {
            Optional<UserCaretaker> uc = userCaretakerRepository.findById(cId);
            if (uc.isEmpty()) {
                throw new UserCaretakerException(Messages.USER_NOT_FOUND);
            }
            uc.get().setReqStatus(true);
            return new CaretakerResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretakerRepository.save(uc.get()));
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public PatientResponse getPatientsUnderMe(String userId,Integer pageNo, Integer pageSize) throws UserCaretakerException {

        try {
            Pageable pageable = PageRequest.of(pageNo,pageSize);
            Page<UserCaretaker> userCaretaker = userCaretakerRepository.getPatientsUnderMe(userId,pageable);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public PatientResponse getPatientRequests(String userId,Integer pageNo , Integer pageSize) throws UserCaretakerException {
        try {
            Pageable pageable = PageRequest.of(pageNo,pageSize);
            Page<UserCaretaker> userCaretaker = userCaretakerRepository.getPatientRequests(userId,pageable);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public CaretakerListResponse getMyCaretakers(String userId,Integer pageNo, Integer pageSize) throws UserCaretakerException {
        try {
            Pageable pageable = PageRequest.of(pageNo,pageSize);
            Page<MyCaretakerDto> userCaretaker = userCaretakerRepository.getMyCaretakers(userId,pageable);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return new CaretakerListResponse(Messages.SUCCESS, Messages.DATA_FOUND, userCaretaker.toList());
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public List<UserCaretaker> getCaretakerRequestStatus(String userId) {
        try {
            return userCaretakerRepository.getCaretakerRequestStatus(userId);
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }


    @Override
    public List<UserCaretaker> getCaretakerRequestsP(String userId) throws UserCaretakerException {
        try {
            List<UserCaretaker> userCaretaker = userCaretakerRepository.getCaretakerRequestsP(userId);
            if (userCaretaker.isEmpty()) {
                throw new UserCaretakerException(Messages.DATA_NOT_FOUND);
            }
            return userCaretaker;
        } catch (DataAccessException dataAccessException) {
            throw new DataAccessExceptionMessage(Messages.SQL_ERROR_MSG + dataAccessException.getMessage());
        }
    }

    @Override
    public PatientRequestResponse delPatientReq(String cId) {


        try {
            Optional<UserCaretaker> userCaretaker = userCaretakerRepository.findById(cId);
            if (userCaretaker.isPresent()) {
                userCaretakerRepository.delete(userCaretaker.get());
                return new PatientRequestResponse(Messages.SUCCESS, Messages.DELETED_SUCCESS);

            }
            return new PatientRequestResponse(Messages.FAILED, Messages.DELETED_SUCCESS);

        } catch (Exception e) {
            return new PatientRequestResponse(Messages.FAILED, Messages.DELETED_SUCCESS);

        }
    }

    @Override
    public SendImageResponse sendImageToCaretaker(MultipartFile multipartFile, String filename, String caretakerid, String medName, Integer medId) throws IOException, UserCaretakerException {

        try {
            
            File file = new File(System.getProperty("user.dir") + "/src/main/upload/static/images");
            if (!file.exists()) {
                file.mkdir();
            }
            Path path = Paths.get(System.getProperty("user.dir") + "/src/main/upload/static/images", filename.concat(".").concat("jpg"));
            Files.write(path, multipartFile.getBytes());

            UserMedicines userMedicines = userMedicineRepository.getMedById(medId);
            String userName = userMedicines.getUserEntity().getUserName();
            Image image = new Image();
            image.setImageUrl(path.getFileName().toString());
            image.setTime(Calendar.getInstance().getTime().toString());
            image.setDate(Calendar.getInstance().getTime());
            image.setUserMedicines(userMedicines);
            image.setCaretakerName(userName);
            imageRepository.save(image);
            String fcmToken = "epkw4MI-RxyMzZjvD6fUl6:APA91bEUyAJpJ5RmDyI1KLcMLJbBPiYSX64oIW4WkNq62zeUlMPUPknGkBHTB_drOBX6CUkiI0Pyfc4Myvt87v6BU69kz0LPq4YM9iWnG9RrNbxIpC4LrtE-zWfNdbB3dbjR2bmogops";
            rabbitTemplate.convertAndSend("project_exchange", "notification_key", new Notificationmessage(fcmToken, "Take medicine", "caretaker", medName, filename + ".jpg"));

        } catch (Exception e) {
            logger.error("CaretakerService" + " :: " + Messages.SQL_ERROR_MSG);
            return new SendImageResponse(Messages.FAILED, Messages.UNABLE_TO_SEND);
        }

        return new SendImageResponse(Messages.SUCCESS, Messages.SENT_SUCCESSFULLY);
    }

    private UserCaretaker mapToEntity(UserCaretakerDTO userCaretakerDTO) {
        return mapper.map(userCaretakerDTO, UserCaretaker.class);

    }

}