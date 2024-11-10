package com.finance.management.Controller;

import com.finance.management.dto.ApiResponseDto;
import com.finance.management.dto.UserDetailsDto;
import com.finance.management.dto.UserLoginDto;
import com.finance.management.service.JwtService;
import com.finance.management.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
public class UserController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private JwtService jwtService;

    
    @PostMapping("/user/register")
    public ResponseEntity<?> createUser(@RequestBody UserDetailsDto userDetailsDto){
       try{
        String result = userManagementService.createUser(userDetailsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, result, null, HttpStatus.CREATED.value(),null));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
    }
}

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponseDto<UserDetailsDto>>userLogin(@RequestBody UserLoginDto userLoginDto) {
        try {
            UserDetailsDto userDetails = userManagementService.userLogin(userLoginDto);
            if (userDetails != null) {
                String token = jwtService.generateToken(userDetails.getEmailId());
                return ResponseEntity.ok(new ApiResponseDto<>(true, userDetails, null, HttpStatus.OK.value(), token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, null, "Invalid email or password", HttpStatus.UNAUTHORIZED.value(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }

    @PostMapping("/api/logout")
    public String logout() {
        return "Logged out successfully";
    }

    }
