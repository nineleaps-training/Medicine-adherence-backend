package com.example.user_service.pojos.dto.caretaker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCaretakerDTO {

    @NotNull(message = "Patientname should not be empty")
    @NotBlank(message = "Patientname should not be empty")
    @Size(min = 3 , max = 40)
    private String patientName;

    @NotNull(message = "RequestStatus could not be null")
    private Boolean reqStatus;

    @NotNull(message = "CareTaker id should be present")
    @NotBlank(message = "CareTaker id should not be empty")
    private String caretakerId;

    @NotNull(message = "Patient id should be present")
    @NotBlank(message = "Patient Id could not be empty")
    private String patientId;

    @NotNull(message = "Caretaker name should be present")
    @NotBlank(message = "CaretTakerName should not be Empty")
    private String caretakerUsername;

    @NotNull
    @NotBlank
    private String sentBy;

}
