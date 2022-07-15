package com.example.user_service.controller.user;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.user_service.config.log.Logger;
import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.exception.user.GoogleSsoException;
import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.MailInfo;
import com.example.user_service.pojos.dto.LoginDTO;
import com.example.user_service.pojos.dto.user.UserEntityDTO;
import com.example.user_service.pojos.dto.user.UserMailDto;
import com.example.user_service.pojos.response.auth.RefreshTokenResponse;
import com.example.user_service.pojos.response.medicine.PdfLinkResponse;
import com.example.user_service.pojos.response.user.UserMailResponse;
import com.example.user_service.pojos.response.user.UserProfileResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.service.user.UserService;
import com.example.user_service.util.JwtUtil;
import com.example.user_service.util.Messages;

@RestController
@RequestMapping(path = "/api/v1")
public class UserController {
    private final UserService    userService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtUtil        jwtUtil;

    UserController(UserService userService, JwtUtil jwtUtil, RabbitTemplate rabbitTemplate) {
        this.jwtUtil        = jwtUtil;
        this.userService    = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // saving the user when they signup

    @PostMapping(
        value    = "/login",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginDTO loginDTO) throws UserExceptionMessage {
        return new ResponseEntity<>(userService.login(loginDTO.getEmail(), loginDTO.getFcmToken()), HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@NotNull @NotBlank @Valid
    @RequestParam(name = "uid") String uid, HttpServletRequest httpServletRequest)
            throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

        if (!token.equals("")) {
            Logger.infoLog("", token);
        }

        String jwtToken = jwtUtil.generateToken(userService.getUserById(uid).getUserName());

        return new ResponseEntity<>(new RefreshTokenResponse(Messages.SUCCESS, jwtToken), HttpStatus.CREATED);
    }

    @PostMapping(
        value    = "/user",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> saveUser(@NotNull
    @NotBlank
    @RequestParam(name = "fcmToken") String fcmToken, @NotNull
    @NotBlank
    @RequestParam(name = "picPath") String picPath, @Valid
    @RequestBody UserEntityDTO userEntityDTO) throws UserExceptionMessage, GoogleSsoException {
        return new ResponseEntity<>(userService.saveUser(userEntityDTO, fcmToken, picPath), HttpStatus.CREATED);
    }

    @GetMapping(value = "/pdf")
    public ResponseEntity<PdfLinkResponse> sendPdf(@RequestParam(name = "medId") Integer medId)
            throws IOException, UserExceptionMessage {
        PdfLinkResponse filePath = userService.sendUserMedicines(medId);

        return new ResponseEntity<>(filePath, HttpStatus.OK);
    }

    // fetching the user with email if not present then sending to that email address
    @GetMapping(
        value    = "/email",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserMailResponse> getUserByEmail(@RequestParam("email") String email,
                                                           @RequestParam("sender") String sender)
            throws UserExceptionMessage {
        UserMailDto userEntity = userService.getUserByEmail(email);

        if (userEntity == null) {
            rabbitTemplate.convertAndSend("project_exchange",
                                          "mail_key",
                                          new MailInfo(email, "Please join", "patient_request", sender));

            return new ResponseEntity<>(new UserMailResponse(Messages.SUCCESS, Messages.MAIL_SENT_SUCCESSFULLY, null),
                                        HttpStatus.OK);
        }

        return new ResponseEntity<>(new UserMailResponse(Messages.SUCCESS, Messages.DATA_FOUND, userEntity),
                                    HttpStatus.OK);
    }

    // fetching user by id
    @GetMapping(
        value    = "/user",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserProfileResponse> getUserById(@RequestParam("userId") String userId)
            throws UserExceptionMessage, UserMedicineException, ExecutionException, InterruptedException {
        List<UserEntity>    user                = Arrays.asList(userService.getUserById(userId));
        List<UserMedicines> list                = user.get(0).getUserMedicines();
        UserProfileResponse userProfileResponse = new UserProfileResponse(Messages.SUCCESS, user, list);

        return new ResponseEntity<>(userProfileResponse, HttpStatus.OK);
    }

    // fetching all the users along with details
    @GetMapping(
        value    = "/users",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<UserEntity>> getUsers()
            throws UserExceptionMessage, ExecutionException, InterruptedException {
        return new ResponseEntity<>(userService.getUsers().get(), HttpStatus.OK);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
