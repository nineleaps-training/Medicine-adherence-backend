package com.example.user_service.exception;

import com.example.user_service.pojos.response.caretaker.CaretakerResponse;
import com.example.user_service.pojos.response.error.GoogleSsoError;
import com.example.user_service.pojos.response.medicine.MedicineResponse;
import com.example.user_service.pojos.response.error.SqlErrorResponse;
import com.example.user_service.pojos.response.user.UserResponse;
import com.example.user_service.util.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;


import javax.validation.ConstraintViolationException;


/**
 * This class is used to send error response
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Returns user exception response
     */
    @ExceptionHandler({UserExceptionMessage.class})
    public ResponseEntity<UserResponse> getuserException(UserExceptionMessage uem, WebRequest webRequest) {
        UserResponse userResponse = new UserResponse(Messages.FAILED, uem.getMessage(), null, "", "");
        return new ResponseEntity<>(userResponse, HttpStatus.NOT_FOUND);

    }

    /**
     * Returns caretaker exception response
     */
    @ExceptionHandler({UserCaretakerException.class})
    public ResponseEntity<CaretakerResponse> getcaretakerexception(UserCaretakerException uce, WebRequest webRequest) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<CaretakerResponse> getcaretakernotvalidexception(ConstraintViolationException uce, WebRequest webRequest) {
        CaretakerResponse caretakerResponse = new CaretakerResponse(Messages.FAILED, uce.getMessage(), null);
        return new ResponseEntity<>(caretakerResponse, HttpStatus.NOT_FOUND);

    }

    /**
     * Returns user medicine exception response
     */
    @ExceptionHandler({UserMedicineException.class})
    public ResponseEntity<MedicineResponse> getUserMedicineException(UserMedicineException udm, WebRequest webRequest) {

        MedicineResponse medicineResponse = new MedicineResponse(Messages.FAILED, udm.getMessage(), null);
        return new ResponseEntity<>(medicineResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({DataAccessExceptionMessage.class})
    public ResponseEntity<SqlErrorResponse> getSqlException(DataAccessExceptionMessage dae, WebRequest webRequest) {
        SqlErrorResponse sqlErrorResponse = new SqlErrorResponse(Messages.FAILED
                , dae.getMessage());

        return new ResponseEntity<>(sqlErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({GoogleSsoException.class})
    public ResponseEntity<GoogleSsoError> getSsoError(GoogleSsoException googleSsoException){

        return new ResponseEntity<>(new GoogleSsoError(Messages.FAILED,googleSsoException.getMessage()),HttpStatus.NOT_FOUND);

    }




}