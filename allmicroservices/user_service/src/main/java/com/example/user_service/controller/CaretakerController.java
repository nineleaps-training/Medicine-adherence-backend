package com.example.user_service.controller;

import com.example.user_service.exception.UserCaretakerException;
import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.exception.UserexceptionMessage;
import com.example.user_service.model.UserCaretaker;
import com.example.user_service.pojos.Notificationmessage;
import com.example.user_service.pojos.caretakerpojos.UserCaretakerpojo;
import com.example.user_service.service.CareTakerService;
import com.example.user_service.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/api/caretaker")
public class CaretakerController {

    @Autowired
    private CareTakerService careTakerService;
    @Autowired
    private UserService userService;

    @Autowired
    RabbitTemplate rabbitTemplate;
    // save caretaker for a patients
    @PostMapping(value = "/savecaretaker" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserCaretaker> saveCaretaker(@RequestBody UserCaretakerpojo userCaretakerpojo){

        return new ResponseEntity<>(careTakerService.saveCareTaker(userCaretakerpojo), HttpStatus.CREATED);

    }

    // update request status if request is accepted or rejected
    @PutMapping(value = "/updatestatus")
    public ResponseEntity<UserCaretaker> updatecaretakerStatus(@RequestParam(name = "c_id") String c_id)
             throws UserCaretakerException {

        return  new ResponseEntity<>(careTakerService.updateCaretakerStatus(c_id), HttpStatus.OK);

    }



    // fetch all the patients of a particular caretaker
    @GetMapping(value = "/myPatients(Caretaker)")
    public ResponseEntity<List<UserCaretaker>> getPatientsUnderMe(@RequestParam(name = "caretaker_id") String  user_id){

        return new ResponseEntity<>(careTakerService.getPatientsUnderMe(user_id),HttpStatus.OK);
    }

    // fetch all the request sent by a patients to a caretaker
    @GetMapping(value = "/patientRequests(Caretaker)")
    public ResponseEntity<List<UserCaretaker>> getPatientRequests(@RequestParam(name = "caretaker_id") String  user_id){

        return new ResponseEntity<>(careTakerService.getPatientRequests(user_id),HttpStatus.OK);

    }

    // where the patients can view all his caretakers
    @GetMapping(value = "/myCareTakers(Patient)")
    public ResponseEntity<List<UserCaretaker>> getMyCaretakers(@RequestParam(name = "patient_id") String  user_id){

        return new ResponseEntity<>(careTakerService.getMyCaretakers(user_id),HttpStatus.OK);
    }

    // to fetch the caretaker request to a patients
    @GetMapping(value = "/caretakerRequests(sentstatus)")
    public ResponseEntity<List<UserCaretaker>> getCaretakerSentStatus(@RequestParam(name = "patient_id") String  user_id){

        return new ResponseEntity<>(careTakerService.getCaretakerRequestStatus(user_id),HttpStatus.OK);

    }



    // to check the status of a request by caretaker

    @GetMapping(value = "/caretakerRequests(for patient)")
    public ResponseEntity<List<UserCaretaker>> getCaretakerRequestsP(@RequestParam(name = "patient_id") String  user_id){

        return new ResponseEntity<>(careTakerService.getCaretakerRequestsP(user_id),HttpStatus.OK);

    }

    @GetMapping(value = "/deletePatientRequest")
    public ResponseEntity<Boolean> delPatientReq(@RequestParam(name = "c_id") String c_id){
        boolean b = careTakerService.delPatientReq(c_id);

        return new ResponseEntity<>(b,HttpStatus.OK);
    }

    @GetMapping(value = "/notifyuser")
    public ResponseEntity<String> notifyuserformed(@RequestParam(name = "fcm_token") String fcm_token , @RequestParam("medname") String body){

        rabbitTemplate.convertAndSend("project_exchange","notification_key",new Notificationmessage(fcm_token,"Take medicine","patient",body,""));
        return new ResponseEntity<>("Ok",HttpStatus.OK);

    }

    @PostMapping(value = "/sendimage")
    public ResponseEntity<String> sendimagetocaretaker(@RequestParam(name = "image") MultipartFile multipartFile
                                                , @RequestParam(name = "name") String filename ,
                                                  @RequestParam(name = "id") String caretaker_id) throws IOException, UserexceptionMessage, UserMedicineException, ExecutionException, InterruptedException {

         File file = new File(System.getProperty("user.dir")+"/src/main/upload/static/images");
         if(!file.exists()){
             file.mkdir();
         }

         Path path = Paths.get(System.getProperty("user.dir")+"/src/main/upload/static/images",filename.concat(".").concat("jpg"));
         Files.write(path,multipartFile.getBytes());
        // String fcm =   userService.getUserById(caretaker_id).getUserDetails().getFcm_token();
      //   System.out.println(fcm);
         String fcm_token = "c_nl_oj2S9S_HmPQjfvDSR:APA91bEYDLIGXU4jI4P26uVqAdoVaaJ378TtGjxrKaytbuqulXWZGs91Jx6_1mrLWEaGECufvZ512BWwQvCAQTnjg3OTh2GPn5E3DNOTh_ycy4Xi7-LZ39OFsGXYjiUm5UDJfRez0CV4";
         rabbitTemplate.convertAndSend("project_exchange","notification_key",new Notificationmessage(fcm_token,"Take medicine","caretaker","",filename+".jpg"));


        return  new ResponseEntity<>("Ok",HttpStatus.OK);

    }

//
}
