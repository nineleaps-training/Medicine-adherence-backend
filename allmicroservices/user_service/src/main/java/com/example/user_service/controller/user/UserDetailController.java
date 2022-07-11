package com.example.user_service.controller.user;



import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.pojos.response.user.UserDetailResponse;
import com.example.user_service.pojos.response.user.UserUpdateDetailResponse;
import com.example.user_service.service.user.UserDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserDetailController {
    private UserDetailService userDetailService;

    public UserDetailController(UserDetailService userDetailService){
        this.userDetailService = userDetailService;
    }
    @PutMapping(value = "/user-details" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDetailResponse> updateUserDetails(@RequestParam("userId") String id,
                                                                @RequestBody UserDetailsDTO userDetailsDTO) throws UserExceptionMessage {
        UserUpdateDetailResponse userDetails = userDetailService.saveUserDetail(id,userDetailsDTO);
        UserDetailResponse userDetailResponse= new UserDetailResponse("Success","Saved user details",userDetails.getUserDetails());
        return new ResponseEntity<>(userDetailResponse,HttpStatus.OK);
    }
}
