package com.example.user_service.controller.medicine;

import com.example.user_service.exception.UserMedicineException;
import com.example.user_service.pojos.dto.medicine.MedicineHistoryDTO;
import com.example.user_service.pojos.dto.medicine.MedicinePojo;
import com.example.user_service.pojos.response.image.ImagesResponse;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.medicine.SyncMedicineHistoryResponse;
import com.example.user_service.pojos.response.sync.SyncResponse;
import com.example.user_service.repository.medicine.UserMedicineRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.service.medicine.UserMedicineService;
import com.example.user_service.util.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/")
public class MedicineController {

    private final UserMedicineService userMedicineService;
    private final UserRepository userRepository;
    private final UserMedicineRepository userMedicineRepository;

    // save caretaker for a patients
    MedicineController(UserMedicineService userMedicineService, UserRepository userRepository, UserMedicineRepository userMedicineRepository) {
        this.userMedicineService = userMedicineService;
        this.userMedicineRepository = userMedicineRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/medicines/sync", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SyncResponse> syncData(@NotNull @NotBlank @RequestParam("userId") String userId, @RequestBody @Valid List<MedicinePojo> medicinePojo) throws UserMedicineException {
        return new ResponseEntity<>(userMedicineService.syncMedicines(userId, medicinePojo), HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/medicine-history/sync")
    public ResponseEntity<SyncMedicineHistoryResponse> syncMedicineHistory(@RequestParam(name = "medId") Integer medId,
                                                                           @RequestBody List<MedicineHistoryDTO> medicineHistory) throws UserMedicineException {

        return new ResponseEntity<>(userMedicineService.syncMedicineHistory(medId, medicineHistory), HttpStatus.OK);
    }

    @GetMapping(value = "/medicine-histories")
    public ResponseEntity<MedicineResponse> getMedicineHistories(@RequestParam(name = "medId") Integer medId) throws UserMedicineException {
        return new ResponseEntity<>(userMedicineService.getMedicineHistory(medId), HttpStatus.OK);
    }

    @GetMapping(value = "/medicine-images")
    public ResponseEntity<ImagesResponse> getMedicineImages(@RequestParam(name = "medId") Integer medId) {
        return new ResponseEntity<>(
                userMedicineService.getUserMedicineImages(medId), HttpStatus.OK);
    }


}
