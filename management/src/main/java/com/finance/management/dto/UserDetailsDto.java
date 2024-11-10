package com.finance.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private String username;
    private String emailId;
    private String password;
    private Integer phoneNumber;
    private String address;

    public UserDetailsDto(String emailId) {
        this.emailId = emailId;
    }
}
