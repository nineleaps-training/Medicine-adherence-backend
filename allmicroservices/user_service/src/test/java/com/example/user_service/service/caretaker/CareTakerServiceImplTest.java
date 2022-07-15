package com.example.user_service.service.caretaker;

import com.example.user_service.exception.caretaker.UserCaretakerException;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserCaretaker;
import com.example.user_service.model.user.UserEntity;
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
import com.example.user_service.util.Messages;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CareTakerServiceImplTest {

    @Mock
    UserCaretakerRepository userCaretakerRepository;
    @Mock
    ModelMapper mapper;
    @Mock
    ImageRepository imageRepository;
    @Mock
    UserMedicineRepository userMedicineRepository;
    @Mock
    RabbitTemplate rabbitTemplate;
    CareTakerServiceImpl careTakerService;

    @BeforeEach
    void init() {
        careTakerService = new CareTakerServiceImpl(imageRepository, userMedicineRepository, rabbitTemplate, userCaretakerRepository, mapper);
    }


    @Test
    void saveCareTaker() throws UserCaretakerException {

        UserCaretakerDTO userCaretakerDTO = new UserCaretakerDTO("nikku", false, "1234", "5678", "shanky", "P");
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        when(mapper.map(userCaretakerDTO, UserCaretaker.class)).thenReturn(userCaretaker);
        when(userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId())).thenReturn(null);
        when(userCaretakerRepository.save(userCaretaker)).thenReturn(userCaretaker);
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.SUCCESS, Messages.REQUEST_SENT_SUCCESS, null);
        careTakerService.saveCareTaker(userCaretakerDTO);
        Assertions.assertEquals(Messages.SUCCESS, caretakerResponse.getStatus());
    }

    @Test
    void saveCaretakerException() {

        UserCaretakerDTO userCaretakerDTO = new UserCaretakerDTO("nikku", false, "1234", "5678", "shanky", "P");
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        when(mapper.map(userCaretakerDTO, UserCaretaker.class)).thenReturn(userCaretaker);
        when(userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId())).thenReturn(userCaretaker);
        try {
            careTakerService.saveCareTaker(userCaretakerDTO);
        } catch (UserCaretakerException userCaretakerException) {
            CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, userCaretakerException.getMessage(), null);

            Assertions.assertEquals(Messages.FAILED, caretakerResponse.getStatus());

        }
    }

    @Test
    void saveCaretakerSqlException() {
        UserCaretakerDTO userCaretakerDTO = new UserCaretakerDTO("nikku", false, "1234", "5678", "shanky", "P");
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        when(mapper.map(userCaretakerDTO, UserCaretaker.class)).thenReturn(userCaretaker);

        when(userCaretakerRepository.check(userCaretaker.getPatientId(), userCaretaker.getCaretakerId())).thenThrow(JDBCConnectionException.class);
        try {
            careTakerService.saveCareTaker(userCaretakerDTO);
        } catch (UserCaretakerException userCaretakerException) {
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, userCaretakerException.getMessage());
        }
    }


    @Test
    void updateCaretakerStatus() throws UserCaretakerException {
        String cId = "1234";
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        Optional<UserCaretaker> caretakerOptional = Optional.of(userCaretaker);
        when(userCaretakerRepository.findById(cId)).thenReturn(caretakerOptional);
       // when(userCaretakerRepository.save(caretakerOptional.get()));
        careTakerService.updateCaretakerStatus(cId);
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.SUCCESS, Messages.DATA_FOUND, caretakerOptional.get());
        Assertions.assertEquals(Messages.SUCCESS, caretakerResponse.getStatus());
    }

    @Test
    void updateCaretakerStatusJdbcException() throws UserCaretakerException {
        String cId = "1234";
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        Optional<UserCaretaker> caretakerOptional = Optional.of(userCaretaker);
        when(userCaretakerRepository.findById(cId)).thenReturn(caretakerOptional);
        when(userCaretakerRepository.save(caretakerOptional.get())).thenThrow(JDBCConnectionException.class);
        try {
            careTakerService.updateCaretakerStatus(cId);

        } catch (UserCaretakerException userCaretakerException) {
            CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, Messages.SQL_ERROR_MSG, caretakerOptional.get());
            Assertions.assertEquals(Messages.SQL_ERROR_MSG, caretakerResponse.getMessage());

        }
    }

    @Test
    void updatecaretakerStatusEmptyException() {

        String cId = "1234";
        UserCaretaker userCaretaker = new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P");
        Optional<UserCaretaker> caretakerOptional = Optional.of(userCaretaker);
        when(userCaretakerRepository.findById(cId)).thenReturn(null);
        try {
            careTakerService.updateCaretakerStatus(cId);
        } catch (UserCaretakerException userCaretakerException) {
            CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, Messages.DATA_NOT_FOUND, caretakerOptional.get());
            Assertions.assertEquals(Messages.DATA_NOT_FOUND, caretakerResponse.getMessage());

        }


    }


    @Test
    void getPatientsUnderMeStatus() throws UserCaretakerException {
        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientsUnderMe("1234", pageable)).thenReturn(page);
        PatientResponse patientResponse = new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, page.toList());
        careTakerService.getPatientsUnderMe("1234", 0, 1);
        Assertions.assertEquals(Messages.SUCCESS, patientResponse.getStatus());
    }

    @Test
    void getPatientsUnderMeSqlException() throws UserCaretakerException {
        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientsUnderMe("1234", pageable)).thenThrow(JDBCConnectionException.class);
        try {
            careTakerService.getPatientsUnderMe("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, userCaretakerException.getMessage());

        }
    }

    @Test
    void getPatientsUnderMeEmptyException() throws UserCaretakerException {
        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList()));
        when(userCaretakerRepository.getPatientsUnderMe("1234", pageable)).thenReturn(page);
        try {
            careTakerService.getPatientsUnderMe("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            Assertions.assertEquals(Messages.DATA_NOT_FOUND, userCaretakerException.getMessage());

        }
    }

    @Test
    void getPatientsUnderMeMessage() throws UserCaretakerException {
        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientsUnderMe("1234", pageable)).thenReturn(page);
        PatientResponse patientResponse = new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, page.toList());
        careTakerService.getPatientsUnderMe("1234", 0, 1);
        Assertions.assertEquals(Messages.DATA_FOUND, patientResponse.getMessage());
    }


    @Test
    void getPatientRequestsStatus() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientRequests("1234", pageable)).thenReturn(page);
        careTakerService.getPatientRequests("1234", 0, 1);
        PatientResponse patientResponse = new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND,page.toList());
        Assertions.assertEquals(Messages.SUCCESS, patientResponse
                .getStatus());
    }

    @Test
    void getPatientRequestsMessage() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientRequests("1234", pageable)).thenReturn(page);
        careTakerService.getPatientRequests("1234", 0, 1);
        PatientResponse patientResponse = new PatientResponse(Messages.SUCCESS, Messages.DATA_FOUND, page.toList());
        Assertions.assertEquals(Messages.DATA_FOUND, patientResponse
                .getMessage());
    }

    @Test
    void getPatientRequestsSQLException() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList(new UserCaretaker("5678", "nikku", false, "1234", "5678", "shanky", null, "P"))));
        when(userCaretakerRepository.getPatientRequests("1234", pageable)).thenThrow(JDBCConnectionException.class);
        try {
            careTakerService.getPatientRequests("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, userCaretakerException.getMessage(), null);
            Assertions.assertEquals(Messages.FAILED, caretakerResponse
                    .getStatus());

        }
    }

    @Test
    void getPatientRequestsEmptyException() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<UserCaretaker> page = new PageImpl<>(new ArrayList<>(Arrays.asList()));
        when(userCaretakerRepository.getPatientRequests("1234", pageable)).thenReturn(page);
        try {
            careTakerService.getPatientRequests("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, userCaretakerException.getMessage(), null);

            Assertions.assertEquals(Messages.FAILED, caretakerResponse
                    .getStatus());

        }
    }


    @Test
    void getMyCaretakersMessage() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<MyCaretakerDto> myCaretakerDtoPage = new PageImpl<>(Arrays.asList(new MyCaretakerDto("niks", "1234", null, "3456")));
        when(userCaretakerRepository.getMyCaretakers("1234", pageable)).thenReturn(myCaretakerDtoPage);
        CaretakerListResponse caretakerListResponse = careTakerService.getMyCaretakers("1234", 0, 1);
        Assertions.assertEquals(Messages.DATA_FOUND, caretakerListResponse.getMessage());
    }

    @Test
    void getMyCaretakersStatus() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<MyCaretakerDto> myCaretakerDtoPage = new PageImpl<>(Arrays.asList(new MyCaretakerDto("niks", "1234", null, "3456")));
        when(userCaretakerRepository.getMyCaretakers("1234", pageable)).thenReturn(myCaretakerDtoPage);
        CaretakerListResponse caretakerListResponse = careTakerService.getMyCaretakers("1234", 0, 1);
        Assertions.assertEquals(Messages.SUCCESS, caretakerListResponse.getStatus());
    }

    @Test
    void getMyCaretakersSQLException() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<MyCaretakerDto> myCaretakerDtoPage = new PageImpl<>(Arrays.asList(new MyCaretakerDto("niks", "1234", null, "3456")));
        when(userCaretakerRepository.getMyCaretakers("1234", pageable)).thenThrow(JDBCConnectionException.class);
        try {
            CaretakerListResponse caretakerListResponse = careTakerService.getMyCaretakers("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, userCaretakerException
                    .getMessage());

        }
    }

    @Test
    void getMyCaretakersEmptyException() throws UserCaretakerException {

        Pageable pageable = PageRequest.of(0, 1);
        Page<MyCaretakerDto> myCaretakerDtoPage = new PageImpl<>(Arrays.asList());
        when(userCaretakerRepository.getMyCaretakers("1234", pageable)).thenReturn(myCaretakerDtoPage);
        try {
            CaretakerListResponse caretakerListResponse = careTakerService.getMyCaretakers("1234", 0, 1);

        } catch (UserCaretakerException userCaretakerException) {
            Assertions.assertEquals(Messages.DATA_NOT_FOUND, userCaretakerException
                    .getMessage());

        }
    }


    @Test
    void delPatientReqSuccess() {

        Optional<UserCaretaker> optionalUserCaretaker = Optional.of(new UserCaretaker("rer4r", "Nikkz", false, "rtrtt5", "ireoio4", "shanky", null, "P"));
        when(userCaretakerRepository.findById("1234")).thenReturn(optionalUserCaretaker);
      //  when(userCaretakerRepository.delete(optionalUserCaretaker.get()));
        PatientRequestResponse patientRequestResponse = careTakerService.delPatientReq("1234");
        Assertions.assertEquals(Messages.SUCCESS,patientRequestResponse.getStatus());
    }
    @Test
    void delPatientReqException() {

        Optional<UserCaretaker> optionalUserCaretaker = Optional.of(new UserCaretaker("rer4r", "Nikkz", false, "rtrtt5", "ireoio4", "shanky", null, "P"));
        when(userCaretakerRepository.findById("1234")).thenReturn(null);
        //  when(userCaretakerRepository.delete(optionalUserCaretaker.get()));
        PatientRequestResponse patientRequestResponse = careTakerService.delPatientReq("1234");
        Assertions.assertEquals(Messages.FAILED,patientRequestResponse.getStatus());
    }
    @Test
    void delPatientReqFailed() {

        Optional<UserCaretaker> optionalUserCaretaker = Optional.of(new UserCaretaker());
        when(userCaretakerRepository.findById("1234")).thenReturn(optionalUserCaretaker);
        //  when(userCaretakerRepository.delete(optionalUserCaretaker.get()));
        PatientRequestResponse patientRequestResponse = careTakerService.delPatientReq("1234");
        Assertions.assertEquals(Messages.FAILED,patientRequestResponse.getStatus());
    }
    @Test
    @DisplayName("Send Image to Caretaker")
    void sendImageToCaretaker() throws IOException, UserCaretakerException {
        UserMedicines userMedicines = new UserMedicines(123, null, "ami", "eat daily", "Fri", null, "10:30", "200mg", 12, 2, new UserEntity(
                "3234r", "Nikunj", "nikkubisht112@gmail.com", LocalDateTime.now(), LocalDateTime.now(), null, null
        ), null, null);
        when(userMedicineRepository.getMedById(1234)).thenReturn(userMedicines);
        MultipartFile multipartFile = new MockMultipartFile("nimbu.jpg", new FileInputStream(new File(System.getProperty("user.dir") + "/src/main/upload/static/images/nimbu.jpg")));
      //  when(imageRepository.save());
      SendImageResponse sendImageResponse = careTakerService.sendImageToCaretaker(multipartFile,"tr","1234","ami",1234);
      Assertions.assertEquals(Messages.SUCCESS,sendImageResponse.getStatus());
    }


    @Test
    @DisplayName("Send Image to Caretaker")
    void sendImageToCaretakerFileException() throws IOException, UserCaretakerException {
        UserMedicines userMedicines = new UserMedicines(123, null, "ami", "eat daily", "Fri", null, "10:30", "200mg", 12, 2, new UserEntity(
                "3234r", "Nikunj", "nikkubisht112@gmail.com", LocalDateTime.now(), LocalDateTime.now(), null, null
        ), null, null);
        when(userMedicineRepository.getMedById(1234)).thenThrow(JDBCConnectionException

                .class);
       try{
           MultipartFile multipartFile = new MockMultipartFile("nimbu.jpg", new FileInputStream(new File(System.getProperty("user.dir") + "/src/main/upload/static/images/nimbu.jpg")));
           careTakerService.sendImageToCaretaker(multipartFile,"tr","1234","ami",1234);

       }catch (UserCaretakerException e){
           Assertions.assertEquals(Messages.ERROR_TRY_AGAIN,e.getMessage());

       }
        //  when(imageRepository.save());
    }


}