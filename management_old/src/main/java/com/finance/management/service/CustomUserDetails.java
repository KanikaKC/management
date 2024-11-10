package com.finance.management.service;

import com.finance.management.entity.UserDetailsEntity;
import com.finance.management.repository.UserManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    private UserManagementRepository userManagementRepository;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        UserDetailsEntity user = userManagementRepository.findByEmailId(emailId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with emailId: " + emailId);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmailId(), user.getPassword(), new ArrayList<>());
    }
}

