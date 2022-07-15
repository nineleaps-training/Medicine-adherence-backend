package com.example.user_service.service.caretaker;

import com.example.user_service.exception.caretaker.UserCaretakerException;
import com.example.user_service.pojos.dto.caretaker.UserCaretakerDTO;
import com.example.user_service.pojos.response.caretaker.CaretakerListResponse;
import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.image.SendImageResponse;
import com.example.user_service.pojos.response.patient.PatientRequestResponse;
import com.example.user_service.pojos.response.patient.PatientResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface CareTakerService {

     CaretakerResponse saveCareTaker(UserCaretakerDTO userCaretakerDTO) throws UserCaretakerException;

     CaretakerResponse updateCaretakerStatus(String cId) throws UserCaretakerException;

     PatientResponse getPatientsUnderMe(String userId,Integer pageNo, Integer pageSize)throws UserCaretakerException;

     PatientResponse getPatientRequests(String userId,Integer pageNo, Integer pageSize) throws UserCaretakerException;

     CaretakerListResponse getMyCaretakers(String userId,Integer pageNo,Integer pageSize) throws UserCaretakerException;

     PatientRequestResponse delPatientReq(String cId);

     SendImageResponse sendImageToCaretaker(MultipartFile multipartFile , String filename , String caretakerId , String medName, Integer medId) throws IOException , UserCaretakerException;
}
