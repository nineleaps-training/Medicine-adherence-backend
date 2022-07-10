package com.example.user_service.service.user;

import com.example.user_service.config.GoogleOauthCheck;
import com.example.user_service.config.PdfMailSender;
import com.example.user_service.exception.GoogleSsoException;
import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Datehelper;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;
import org.hibernate.HibernateException;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMedicineRepository userMedicineRepository;

    @Mock
    private UserMedicineService userMedicineService;
    @Mock
    private UserDetailsRepository userDetailsRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private PdfMailSender pdfMailSender;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GoogleOauthCheck googleOauthCheck;

    @BeforeEach
    public void init() {
        userServiceImpl = new UserServiceImpl(userRepository, userMedicineRepository, userDetailsRepository, pdfMailSender, googleOauthCheck, passwordEncoder, jwtUtil,mapper);
    }


    @Test
    void getUserById() throws UserExceptionMessage {
        UserEntity userEntity = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2", "vinay", "vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(), null, null);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(userEntity);
        UserEntity userEntity1 = userServiceImpl.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2");
        Assertions.assertEquals(userEntity.getUserId(), userEntity1.getUserId());
    }

    @Test
    void getUserIdSqlException(){
        when(userRepository.getUserById("gtgt45")).thenThrow(JDBCConnectionException.class);
        try {
            userServiceImpl.getUserById("gtgt45");
        }catch (UserExceptionMessage userExceptionMessage){
               Assertions.assertEquals(Messages.ERROR_TRY_AGAIN,userExceptionMessage.getMessage());
        }
    }
    @Test
    void getUserIdException(){
        UserEntity userEntityTest = new UserEntity("gtgt45","Nikunj","nikkubisht112@gmail.com",LocalDateTime.now(),LocalDateTime.now(),null,null);
        when(userRepository.getUserById("gtgt45")).thenReturn(null);
        try {
            userServiceImpl.getUserById("gtgt45");
        }catch (UserExceptionMessage userExceptionMessage){
            Assertions.assertEquals(Messages.DATA_NOT_FOUND,userExceptionMessage.getMessage());
        }
    }
    @Test
    void updateUser() {
    }

    @Test
    void getUserByName() {
    }

    @Test
    void getUserByEmail() throws UserExceptionMessage {

        UserMailDto userMailDto = new UserMailDto("nikunj", "bishtnikunj94@gmail.com", "https://lh3.googleusercontent.com/a/AATXAJx7RSOEmRl0vOZsVggFylqXQiBSCJMpqWvCP3Q=s96-c");
        when(userRepository.searchByMail("bishtnikunj94@gmail.com")).thenReturn(userMailDto);
        UserMailDto userMailDtoTest = userServiceImpl.getUserByEmail("bishtnikunj94@gmail.com");
        Assertions.assertEquals(userMailDto.getUserName(), userMailDtoTest.getUserName());

    }

    @Test
    void getUserByMailExceptionTest() throws UserExceptionMessage {
        when(userRepository.searchByMail("bishtnikunj94@gmail.com")).thenThrow(JDBCConnectionException.class);
        try {
            userServiceImpl.getUserByEmail("bishtnikunj94@gmail.com");
        } catch (DataAccessException | HibernateException e) {
            UserResponse userResponse = new UserResponse(Messages.FAILED, e.getMessage(), null, "", "");
            Assertions.assertEquals(userResponse.getMessage(), e.getMessage());
        }
    }

    @Test
    @DisplayName("Check for success pdf response")
    void sendUserMedicines() throws UserExceptionMessage {
        UserMedicines userMedicines = new UserMedicines(71581, "2022-05-27T04:12:31.339Z", "Glimeperide", "Take with water", "Fri", "2022-06-09T04:13:40.289Z", "9:44 AM-9:46 AM", "Take 20mg of glim med", 4, 2, null, null, null);
        when(userMedicineRepository.findById(71581)).thenReturn(Optional.of(userMedicines));
        PdfLinkResponse pdfLinkResponse = userServiceImpl.sendUserMedicines(71581);
        PdfLinkResponse pdfLinkResponseTest = new PdfLinkResponse(Messages.SUCCESS, Messages.PDF_SUCCESS, "");
        Assertions.assertEquals(pdfLinkResponse.getMessage(), pdfLinkResponseTest.getMessage());

    }

    @Test
    @DisplayName("Failure for pdf response")
    void sendUserMedicineFailure() throws UserExceptionMessage {
        UserMedicines userMedicines = new UserMedicines();
        PdfLinkResponse pdfLinkResponse = new PdfLinkResponse(Messages.FAILED, Messages.ERROR_TRY_AGAIN, null);
        PdfLinkResponse pdfLinkResponseTest = userServiceImpl.sendUserMedicines(71581);
        Assertions.assertEquals(pdfLinkResponse, pdfLinkResponseTest);
    }


    @Test
    void login() throws UserExceptionMessage {
        UserEntity userEntityTest = new UserEntity("32526437373625322", "nikunj", "bishtnikunj94@gmail.com", LocalDateTime.now(), LocalDateTime.now(), new UserDetails("", "", "", 23, "", 43, 3223L, 234324L, 343.34F, "", "", "", "", 34, 34324, "", new UserEntity()), null);
        when(userRepository.findByMail("bishtnikunj94@gmail.com")).thenReturn(userEntityTest);
        when(jwtUtil.generateToken(any())).thenReturn("ufdafya");
        when(passwordEncoder.encode(any())).thenReturn("siyfif");
        UserResponse userResponse = new UserResponse(Messages.SUCCESS, Messages.ACCOUNT_CREATED, null, "", "");
        UserResponse userResponseTest = userServiceImpl.login("bishtnikunj94@gmail.com", "fdfd");
        Assertions.assertEquals(userResponse.getMessage(), userResponseTest.getMessage());

    }

    @Test
    @DisplayName("Login for Exception")
    void loginexception() throws UserExceptionMessage {
        UserEntity userEntityTest = new UserEntity();
        when(userRepository.findByMail("bishtnik@gmail.com")).thenReturn(null);
        try {
            userServiceImpl.login("bishtnik@gmail.com", "fdfd");
        } catch (UserExceptionMessage userExceptionMessage) {
            Assertions.assertEquals(Messages.DATA_NOT_FOUND, userExceptionMessage
                    .getMessage());

        }
    }

    @Test
    void loginSqlException() throws UserExceptionMessage {
        //  UserEntity userEntityTest = new UserEntity("32526437373625322", "nikunj", "n@gmail.com", LocalDateTime.now(), LocalDateTime.now(), new UserDetails("", "", "", 23, "", 43, 3223L, 234324L, 343.34F, "", "", "", "", 34, 34324, "", null), null);
        when(userRepository.findByMail("n@gmail.com")).thenThrow(HibernateException.class);
        try {
            userServiceImpl.login("n@gmail.com", "fdfdfdf");
        } catch (UserExceptionMessage hibernateException) {
            UserResponse userResponse = new UserResponse(Messages.FAILED, hibernateException.getMessage(), null, "", "");
            Assertions.assertEquals(userResponse.getStatus(), Messages.FAILED);

        }
    }

    @Test
    void loginStatusTest() throws UserExceptionMessage {
        //  UserEntity userEntityTest = new UserEntity("32526437373625322", "nikunj", "n@gmail.com", LocalDateTime.now(), LocalDateTime.now(), new UserDetails("", "", "", 23, "", 43, 3223L, 234324L, 343.34F, "", "", "", "", 34, 34324, "", null), null);
        when(userRepository.findByMail("n@gmail.com")).thenThrow(HibernateException.class);
        try {
            userServiceImpl.login("n@gmail.com", "fdfdfdf");
        } catch (UserExceptionMessage hibernateException) {
            UserResponse userResponse = new UserResponse(Messages.FAILED, hibernateException.getMessage(), null, "", "");
            Assertions.assertEquals(userResponse.getStatus(), Messages.FAILED);

        }
    }

    @Test
    @DisplayName("Save User Test")
    void alreadysavedUsertest() throws UserExceptionMessage, GoogleSsoException {

        UserResponse userResponse = new UserResponse(Messages.SUCCESS, Messages.SAVED_USER_SUCCESSFULLY, null, "jwtToken", "refreshToken");
        UserEntityDTO userEntityDTO = new UserEntityDTO("Nikunj", "nikkubisht112@gmail.com");
        when(userRepository.findByMail("nikkubisht112@gmail.com")).thenReturn(new UserEntity());
        userServiceImpl.saveUser(userEntityDTO, "dfdfdf", "fefcdfe");
        UserResponse userresponseTest = new UserResponse(Messages.FAILED, Messages.USER_ALREADY_PRESENT, new ArrayList<>(Arrays.asList(new UserEntity())), "", "");
        Assertions.assertEquals(Messages.FAILED, userresponseTest.getStatus());
    }

    @Test
    @DisplayName("New User Test")
    void newuserTest() throws UserExceptionMessage, GoogleSsoException {

        UserEntityDTO userEntityDTO = new UserEntityDTO("Nikunj", "nikkubisht112@gmail.com");
        UserEntity userEntityTest = new UserEntity("deefrfssdfe","Nikunj", "nikkubisht112@gmail.com",LocalDateTime.now(),LocalDateTime.now(),new UserDetails(),null);
        when(userRepository.findByMail("nikkubisht112@gmail.com")).thenReturn(null);
        when(mapper.map(userEntityDTO,UserEntity.class)).thenReturn(userEntityTest);
        when(userRepository.save(userEntityTest)).thenReturn(userEntityTest);
        when(jwtUtil.generateToken(any())).thenReturn("jwjw");
        when(passwordEncoder.encode(any())).thenReturn("psps");
        userServiceImpl.saveUser(userEntityDTO,"frf","frfr");
        UserResponse userResponse = new UserResponse(Messages.SUCCESS, Messages.SAVED_USER_SUCCESSFULLY, new ArrayList<>(Arrays.asList()), "jwjw", "pwpw");
        Assertions.assertEquals(userResponse.getStatus(),Messages.SUCCESS);
    }

    @Test
    @DisplayName("Save User Exception")
    void savenewUserException(){
        UserEntityDTO userEntityDTO = new UserEntityDTO("Nikunj", "nikkubisht112@gmail.com");
        when(userRepository.findByMail("nikkubisht112@gmail.com")).thenThrow(JDBCConnectionException.class);
        try{
            userServiceImpl.saveUser(userEntityDTO,"fdfd","fdfs");
        }catch (JDBCConnectionException |UserExceptionMessage |GoogleSsoException userExceptionMessage){
            UserResponse userResponse = new UserResponse(Messages.FAILED, userExceptionMessage.getMessage(), null, "", "");
            Assertions.assertEquals(Messages.FAILED,userResponse.getStatus());
        }
    }
    @Test
    @DisplayName("Save User Excception")
    void saveUserException() throws UserExceptionMessage, GoogleSsoException {
        UserEntityDTO userEntityDTO = new UserEntityDTO("Nikunj", "nikkubisht112@gmail.com");
        UserEntity userEntityTest = new UserEntity("deefrfssdfe","Nikunj", "nikkubisht112@gmail.com",LocalDateTime.now(),LocalDateTime.now(),new UserDetails(),null);
        when(userRepository.findByMail("nikkubisht112@gmail.com")).thenReturn(null);
        when(mapper.map(userEntityDTO,UserEntity.class)).thenReturn(userEntityTest);
        when(userRepository.save(userEntityTest)).thenReturn(new UserEntity());

       try{
           userServiceImpl.saveUser(userEntityDTO,"frf","frfr");

       }catch (JDBCConnectionException | UserExceptionMessage userExceptionMessage) {
           UserResponse userResponse = new UserResponse(Messages.FAILED, Messages.ERROR_TRY_AGAIN, null, "", "");
           Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, userResponse.getMessage());
       }

    }

    @Test
    @DisplayName("Get Users Test")
    void getUsers() throws UserExceptionMessage, ExecutionException, InterruptedException {

        Pageable pageable = PageRequest.of(0,1);
        List<UserEntity> list = new ArrayList<>(Arrays.asList(new UserEntity("deefrfssdfe","Nikunj", "nikkubisht112@gmail.com",LocalDateTime.now(),LocalDateTime.now(),new UserDetails(),null),new UserEntity("deefrfssdfe","Nikunj", "nikkubisht112@gmail.com",LocalDateTime.now(),LocalDateTime.now(),new UserDetails(),null)));
        when(userRepository.findAllUsers(pageable)).thenReturn(list);

        CompletableFuture<List<UserEntity>> listCompletableFuture = userServiceImpl.getUsers();
        Assertions.assertEquals(list.get(0),listCompletableFuture.get().get(0));

    }
    @Test
    @DisplayName("Get Users Test")
    void getUsersSqlException() throws UserExceptionMessage, ExecutionException, InterruptedException {

        Pageable pageable = PageRequest.of(0, 1);
        List<UserEntity> list = new ArrayList<>(Arrays.asList(new UserEntity("deefrfssdfe", "Nikunj", "nikkubisht112@gmail.com", LocalDateTime.now(), LocalDateTime.now(), new UserDetails(), null), new UserEntity("deefrfssdfe", "Nikunj", "nikkubisht112@gmail.com", LocalDateTime.now(), LocalDateTime.now(), new UserDetails(), null)));
        when(userRepository.findAllUsers(pageable)).thenThrow(JDBCConnectionException.class);
        try {
            userServiceImpl.getUsers();
        } catch (JDBCConnectionException | UserExceptionMessage jdbcConnectionException) {
            Assertions.assertEquals(Messages.ERROR_TRY_AGAIN, jdbcConnectionException.getMessage());

        }
    }

}