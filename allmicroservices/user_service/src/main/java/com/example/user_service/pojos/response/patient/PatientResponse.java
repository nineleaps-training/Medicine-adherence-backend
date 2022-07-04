package com.example.user_service.pojos.response.patient;

import com.example.user_service.model.user.UserCaretaker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponse {

    private String status;
    private String message;
    private List<UserCaretaker> patientsList;

}
