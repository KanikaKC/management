package com.finance.management.service;

import com.finance.management.dto.UserDetailsDto;
import com.finance.management.dto.UserLoginDto;
import com.finance.management.entity.UserDetailsEntity;
import com.finance.management.repository.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    @Autowired
    private UserManagementRepository userManagementRepository;


    public String createUser(UserDetailsDto userDetailsDto){
        UserDetailsEntity entity =  new UserDetailsEntity();
        entity.setUsername(userDetailsDto.getUsername());
        entity.setEmailId(userDetailsDto.getEmailId());
        entity.setPassword(userDetailsDto.getPassword());
        entity.setPhoneNumber(userDetailsDto.getPhoneNumber());
        entity.setAddress(userDetailsDto.getAddress());
        if (userManagementRepository.existsByEmailId(entity.getEmailId())){
            throw new IllegalArgumentException("User already exists with email: " + userDetailsDto.getEmailId());
        }
        userManagementRepository.save(entity);
        return  "User Signup successfully";
    }

    public UserDetailsDto userLogin(UserLoginDto userLoginDto) {
       UserDetailsEntity userData = userManagementRepository.findByEmailIdAndPassword(userLoginDto.getEmailId(), userLoginDto.getPassword());
        if (userData == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
       UserDetailsDto userDetailsDto = null;
       if(userData!=null){
        userDetailsDto = new UserDetailsDto();
        userDetailsDto.setUsername(userData.getUsername());
        userDetailsDto.setEmailId(userData.getEmailId());
       }
       return userDetailsDto;

    }

    public UserDetailsDto findOrCreateUserByEmail(String email, String username) {
        UserDetailsEntity user = userManagementRepository.findByEmailId(email);

        if (user == null) {
            user = new UserDetailsEntity();
            user.setEmailId(email);
            user.setUsername(username);
            userManagementRepository.save(user);
        }

        return new UserDetailsDto(user.getUsername(), user.getEmailId(), null, user.getPhoneNumber(), user.getAddress());
    }


}