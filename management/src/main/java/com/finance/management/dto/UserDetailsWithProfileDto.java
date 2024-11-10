package com.finance.management.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

@Data
@NoArgsConstructor
public class UserDetailsWithProfileDto {

        private String username;
        private String emailId;
        private Integer phoneNumber;
        private String address;
        private String profilePic;
    public UserDetailsWithProfileDto(String username, String emailId, Integer phoneNumber, String address, byte[] profilePicData) {
        this.username = username;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.profilePic = (profilePicData != null) ? Base64.getEncoder().encodeToString(profilePicData) : null;
       }

}
