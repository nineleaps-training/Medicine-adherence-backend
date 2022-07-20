package com.example.user_service.controller.medicine;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.user_service.exception.medicine.UserMedicineException;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.image.ImagesResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineHistoryResponse;
import com.example.user_service.pojos.response.sync.SyncResponse;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Messages;

@RestController
@RequestMapping(path = "/api/v1/")
public class MedicineController {
    private final UserMedicineService userMedicineService;

    // save caretaker for a patients
    MedicineController(UserMedicineService userMedicineService) {
        this.userMedicineService = userMedicineService;
    }

    @PostMapping(
        value    = "/medicines/sync",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SyncResponse> syncData(@NotNull
    @NotBlank
    @RequestParam("userId") String userId, @RequestBody
    @Valid List<MedicinePojo> medicinePojo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new SyncResponse(Messages.FAILED,
                                                         Objects.requireNonNull(
                                                             bindingResult.getFieldError()).getDefaultMessage(),null),
                                        HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(userMedicineService.syncMedicines(userId, medicinePojo), HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/medicine-history/sync")
    public ResponseEntity<SyncMedicineHistoryResponse> syncMedicineHistory(@Min(
        value   = 1,
        message = "Enter Valid Medicine Id"
    )
    @RequestParam(name = "medId") Integer medId, @Valid
    @RequestBody List<MedicineHistoryDTO> medicineHistory, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new SyncMedicineHistoryResponse(Messages.FAILED,
                                                                        Objects.requireNonNull(
                                                                        bindingResult.getFieldError()).getDefaultMessage()),
                                        HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(userMedicineService.syncMedicineHistory(medId, medicineHistory), HttpStatus.OK);
    }

    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@Min(
        value   = 1,
        message = "Enter Valid Id"
    )
    @RequestParam(name = "medId") Integer medId)  {


        return new ResponseEntity<>(userMedicineService.getMedicineHistory(medId), HttpStatus.OK);
    }

    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImagesResponse> getMedicineImages(@Min(
        value   = 1,
        message = "Enter valid medicine id"
    )
    @RequestParam(name = "medId") Integer medId, BindingResult bindingResult)  {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new ImagesResponse(Messages.FAILED,
                                                           Objects.requireNonNull(
                                                               bindingResult.getFieldError()).getDefaultMessage(),
                                                           null),
                                        HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<>(userMedicineService.getUserMedicineImages(medId), HttpStatus.OK);
    }
}


