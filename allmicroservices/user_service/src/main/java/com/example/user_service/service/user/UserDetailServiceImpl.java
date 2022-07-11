package com.example.user_service.service.user;

import com.example.user_service.exception.UserExceptionMessage;
import com.example.user_service.model.user.UserDetails;
import com.example.user_service.model.user.UserEntity;
import com.example.user_service.pojos.dto.user.UserDetailsDTO;
import com.example.user_service.pojos.response.user.UserUpdateDetailResponse;
import com.example.user_service.repository.user.UserDetailsRepository;
import com.example.user_service.repository.user.UserRepository;
import com.example.user_service.util.Messages;
import org.hibernate.exception.JDBCConnectionException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailService {
    private UserDetailsRepository userDetailsRepository;
    private UserRepository userRepository;

    UserDetailServiceImpl(UserDetailsRepository userDetailsRepository , UserRepository userRepository){
        this.userDetailsRepository = userDetailsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserUpdateDetailResponse saveUserDetail(String id, UserDetailsDTO userDetailsDTO) throws UserExceptionMessage {
        try {
            Optional<UserEntity> user = Optional.ofNullable(userRepository.getUserById(id));
            if (user.isEmpty()) {
                throw new UserExceptionMessage("User not found");
            }
            UserDetails userDetails1 = user.get().getUserDetails();
            userDetails1.setAge(userDetailsDTO.getAge());
            userDetails1.setBloodGroup(userDetailsDTO.getBloodGroup());
            userDetails1.setBio(userDetailsDTO.getBio());
            userDetails1.setGender(userDetailsDTO.getGender());
            userDetails1.setWeight(userDetailsDTO.getWeight());
            userDetails1.setMartialStatus(userDetailsDTO.getMartialStatus());
            userDetails1.setUserContact(userDetailsDTO.getUserContact());
            return new UserUpdateDetailResponse(Messages.SUCCESS,Messages.DATA_FOUND,userDetailsRepository.save(userDetails1));
        } catch (DataAccessException | JDBCConnectionException dataAccessException) {
            throw new UserExceptionMessage(Messages.ERROR_TRY_AGAIN);
        }
    }
}