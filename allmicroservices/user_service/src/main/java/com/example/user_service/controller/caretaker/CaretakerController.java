package com.example.user_service.controller.caretaker;

import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.dto.caretaker.SendImageDto;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerListResponse;
import com.example.user_service.pojos.response.image.SendImageResponse;
import com.example.user_service.pojos.response.patient.PatientRequestResponse;
import com.example.user_service.pojos.response.patient.PatientResponse;
import com.example.user_service.service.caretaker.CareTakerService;
import com.example.user_service.util.Messages;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping(path = "/api/v1")
@Validated
public class CaretakerController {

    private final CareTakerService careTakerService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${project.rabbitmq.exchange}")
    private String topicExchange;
    @Value("${project.rabbitmq.routingkey2}")
    private String routingKey2;


    public CaretakerController(CareTakerService careTakerService, RabbitTemplate rabbitTemplate) {
        this.careTakerService = careTakerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // save caretaker for a patients
    @PostMapping(value = "/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CaretakerResponse> saveCaretaker(@Valid @RequestBody UserCaretakerDTO userCaretakerDTO , BindingResult bindingResult) throws UserCaretakerException {

        if(bindingResult.hasErrors()){

            return new ResponseEntity<>(new CaretakerResponse(Messages.FAILED,  Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),null),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(careTakerService.saveCareTaker(userCaretakerDTO), HttpStatus.OK);
    }

    // update request status if request is accepted or rejected
    @PutMapping(value = "/accept")
    public ResponseEntity<CaretakerResponse> updateCaretakerStatus(@NotNull @NotBlank @RequestParam(name = "cId") String cId ,  BindingResult bindingResult)
            throws UserCaretakerException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(new CaretakerResponse(Messages.FAILED,bindingResult.getFieldError().getDefaultMessage(),null),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(careTakerService.updateCaretakerStatus(cId), HttpStatus.OK);
    }


    // fetch all the patients of a particular caretaker
    @GetMapping(value = "/patients")
    public ResponseEntity<PatientResponse> getPatientsUnderMe(@RequestParam(name = "caretakerId") @NotNull @Size(max = 5) @NotBlank String userId
                                                            , @NotNull @NotBlank Integer pageNo , @NotNull @NotBlank Integer pageSize
                                                            , BindingResult bindingResult)
            throws UserCaretakerException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(new PatientResponse(Messages.FAILED,bindingResult.getFieldError().getDefaultMessage(),null),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(careTakerService.getPatientsUnderMe(userId,pageNo,pageSize), HttpStatus.OK);
    }

    // fetch all the request sent by a patients to a caretaker
    @GetMapping(value = "/patient/requests")
    public ResponseEntity<PatientResponse> getPatientRequestsC(@NotNull @NotBlank @RequestParam(name = "caretakerId") String userId
                                                              ,@RequestParam(name = "pageNo") Integer pageNo,@RequestParam(name = "pageSize") Integer pageSize
                                                              ,BindingResult bindingResult) throws UserCaretakerException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(new PatientResponse(Messages.FAILED,bindingResult.getFieldError().getDefaultMessage(),null),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(careTakerService.getPatientRequests(userId,pageNo,pageSize), HttpStatus.OK);

    }

    // where the patients can view all his caretakers
    @GetMapping(value = "/caretakers")
    public ResponseEntity<CaretakerListResponse> getMyCaretakers(@NotNull @NotBlank @RequestParam(name = "patientId") String userId,@NotNull @NotBlank @RequestParam(name = "pageNo") Integer pageNo,@NotNull @NotBlank @RequestParam(name = "pageSize") Integer pageSize) throws UserCaretakerException {
        return new ResponseEntity<>(careTakerService.getMyCaretakers(userId,pageNo,pageSize), HttpStatus.OK);
    }
    // to check the status of a request by caretaker

    @GetMapping(value = "/delete")
    public ResponseEntity<PatientRequestResponse> delPatientReq(@NotNull @NotBlank @RequestParam(name = "cId") String cId , BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(new PatientRequestResponse(Messages.FAILED,bindingResult.getFieldError().getDefaultMessage()),HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(careTakerService.delPatientReq(cId), HttpStatus.OK);
    }

    @GetMapping(value = "/notifyuser")
    public ResponseEntity<String> notifyUserForMed(@NotNull @NotBlank @RequestParam(name = "fcmToken") String fcmToken,@NotNull @NotBlank @RequestParam("medname") String body , BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(Messages.FAILED,HttpStatus.NOT_ACCEPTABLE);
        }
        rabbitTemplate.convertAndSend(topicExchange, routingKey2 , new Notificationmessage(fcmToken, Messages.TAKE_MEDICINE, Messages.PATIENT, body, ""));
        return new ResponseEntity<>(Messages.SUCCESS, HttpStatus.OK);
    }

    @PostMapping(value = "/image")
    @Transactional(timeout = 10)
    public ResponseEntity<SendImageResponse> sendImageToCaretaker(@Valid @ModelAttribute SendImageDto sendImageDto , BindingResult bindingResult) throws IOException, UserCaretakerException {
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(new SendImageResponse(Messages.FAILED,bindingResult.getFieldError().getDefaultMessage()),HttpStatus.NOT_ACCEPTABLE);
        }
        SendImageResponse sendImageResponse = careTakerService.sendImageToCaretaker(sendImageDto.getImage(), sendImageDto.getName(), sendImageDto.getId(), sendImageDto.getMedName(), sendImageDto.getMedId());
        return new ResponseEntity<>(sendImageResponse, HttpStatus.OK);

    }
}