package com.example.user_service.service.user;

import com.example.user_service.exception.user.UserExceptionMessage;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.pojos.response.user.UserUpdateDetailResponse;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Messages;
import org.hibernate.exception.JDBCConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)

class UserDetailServiceImplTest {

    @Mock
    UserDetailsRepository userDetailsRepository;

    @Mock
    UserRepository userRepository;

    UserDetailServiceImpl userDetailService;

    @BeforeEach
    void UserDetailServiceImplTest(){
            userDetailService = new UserDetailServiceImpl(
                    userDetailsRepository,userRepository
            );
    }

    @Test
    void saveUserDetailTest() throws UserExceptionMessage {
        UserDetailsDTO userDetailsDTO= new UserDetailsDTO("Something",21,null,"male","AB+","Unmarried",60);
        when(userRepository.getUserById("feyiafiafgiagfieagfi")).thenReturn(null);
        try {
            userDetailService.saveUserDetail("feyiafiafgiagfieagfi", userDetailsDTO);
        }catch(UserExceptionMessage userExceptionMessage) {
            Assertions.assertEquals("User not found",userExceptionMessage.getMessage());
        }
    }

        @Test
        void saveUserDetailTestStatus() throws UserExceptionMessage {
            UserDetailsDTO userDetailsDTO= new UserDetailsDTO("Something",21,null,"male","AB+","Unmarried",60);
            when(userRepository.getUserById("feyiafiafgiagfieagfi")).thenReturn(null);
            try {
                userDetailService.saveUserDetail("feyiafiafgiagfieagfi", userDetailsDTO);
            }catch(UserExceptionMessage userExceptionMessage) {
                Assertions.assertEquals("User not found",userExceptionMessage.getMessage());
            }
        }

        @Test
        void saveUserDetailExceptionMessage() throws UserExceptionMessage {
            UserDetailsDTO userDetailsDTO = new UserDetailsDTO("Something", 21, null, "male", "AB+", "Unmarried", 60);
            UserDetails userDetails = new UserDetails("73578dfd-e7c9-4381-a348-113e72d80fa2","something",null,21,null,414124,null,5L,56L,null,"male","AB+","Unmarried",60,2314,null,null);
            UserEntity user = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
            when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenThrow(JDBCConnectionException.class);
            try{
                UserUpdateDetailResponse userDetails1= userDetailService.saveUserDetail("73578dfd-e7c9-4381-a348-113e72d80fa2",userDetailsDTO);

            }catch (UserExceptionMessage userExceptionMessage){
                Assertions.assertEquals(Messages.ERROR_TRY_AGAIN,userExceptionMessage.getMessage());

            }
        }

    @Test
    void saveUserDetailExceptionTest() throws UserExceptionMessage {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO("Something", 21, null, "male", "AB+", "Unmarried", 60);
        UserDetails userDetails = new UserDetails("73578dfd-e7c9-4381-a348-113e72d80fa2","something",null,21,null,414124,null,5L,56L,null,"male","AB+","Unmarried",60,2314,null,null);
        UserEntity user = new UserEntity("73578dfd-e7c9-4381-a348-113e72d80fa2","vinay","vinay@gmail.com", LocalDateTime.now(), LocalDateTime.now(),userDetails,null);
        when(userRepository.getUserById("73578dfd-e7c9-4381-a348-113e72d80fa2")).thenReturn(user);
        UserUpdateDetailResponse userDetails1= userDetailService.saveUserDetail("73578dfd-e7c9-4381-a348-113e72d80fa2",userDetailsDTO);
        Assertions.assertEquals(Messages.SUCCESS,userDetails1.getStatus());
    }



}