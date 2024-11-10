package com.finance.management.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.management.config.RabbitConfig;
import com.finance.management.dto.UserDetailsDto;
import com.finance.management.dto.UserDetailsWithProfileDto;
import com.finance.management.dto.UserLoginDto;
import com.finance.management.dto.UserResponseDto;
import com.finance.management.entity.UserDetailsEntity;
import com.finance.management.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishUserDetails(UserLoginDto userLoginDto) {
        System.out.println("Sending UserLoginDto: " + userLoginDto);
        rabbitTemplate.convertAndSend(RabbitConfig.USER_QUEUE, userLoginDto);
    }

    public UserDetailsEntity findByEmail(String email) {
        return userRepository.findByEmailId(email);
    }

    public void saveUser(UserDetailsEntity user) {
        userRepository.save(user);
    }

    public void updateUser(UserDetailsEntity user) {
        userRepository.save(user);
    }
    public String createUser(UserDetailsDto userDetailsDto){
        UserDetailsEntity entity =  new UserDetailsEntity();
        entity.setUsername(userDetailsDto.getUsername());
        entity.setEmailId(userDetailsDto.getEmailId());
        entity.setPassword(userDetailsDto.getPassword());
        entity.setPhoneNumber(userDetailsDto.getPhoneNumber());
        entity.setAddress(userDetailsDto.getAddress());
        if (userRepository.existsByEmailId(entity.getEmailId())){
            throw new IllegalArgumentException("User already exists with email: " + userDetailsDto.getEmailId());
        }
        userRepository.save(entity);
        return  "User Signup successfully";
    }

    public UserDetailsWithProfileDto userLogin(UserLoginDto userLoginDto) {
        UserDetailsEntity userData = userRepository.findByEmailIdAndPassword(userLoginDto.getEmailId(), userLoginDto.getPassword());
        if (userData == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        UserDetailsWithProfileDto userDetailsWithProfileDto = new UserDetailsWithProfileDto(
                userData.getUsername(),
                userData.getEmailId(),
                userData.getPhoneNumber(),
                userData.getAddress(),
                userData.getProfilePic()
        );

        return userDetailsWithProfileDto;
    }

    public UserDetailsWithProfileDto getUserDetailsByEmail(String emailId) {
        UserDetailsEntity user = userRepository.findByEmailId(emailId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        // Create DTO with profilePic encoded as Base64 string
        return new UserDetailsWithProfileDto(
                user.getUsername(),
                user.getEmailId(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getProfilePic()  // Byte array converted to Base64
        );
    }

    public String updateUser(String emailId, UserDetailsDto updatedUserDetailsDto, MultipartFile profilePic) throws Exception {
        // Fetch the user by email
        UserDetailsEntity user = userRepository.findByEmailId(emailId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Update user details
        user.setUsername(updatedUserDetailsDto.getUsername());
        user.setPhoneNumber(updatedUserDetailsDto.getPhoneNumber());
        user.setAddress(updatedUserDetailsDto.getAddress());
        user.setEmailId(updatedUserDetailsDto.getEmailId());

        // Handle profile picture update if a new one is provided
        if (profilePic != null && !profilePic.isEmpty()) {
            byte[] profilePicBytes = profilePic.getBytes();
            user.setProfilePic(profilePicBytes);
        }

        // Save the updated user entity
        userRepository.save(user);
        return "User updated successfully";
    }
}