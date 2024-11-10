package com.finance.login.Controller;



import com.finance.login.dto.ApiResponseDto;
import com.finance.login.dto.UserDetailsDto;
import com.finance.login.dto.UserDetailsWithProfileDto;
import com.finance.login.dto.UserLoginDto;
import com.finance.login.service.JwtService;
import com.finance.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/user/register")
    public ResponseEntity<ApiResponseDto<String>> createUser(@RequestBody UserDetailsDto userDetailsDto) {
        try {
            String result = userService.createUser(userDetailsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, result, null, HttpStatus.CREATED.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @PostMapping("/user/login")
    public ResponseEntity<ApiResponseDto<UserDetailsWithProfileDto>> userLogin(@RequestBody UserLoginDto userLoginDto) {
        try {
            UserDetailsWithProfileDto userDetailsWithProfileDto = userService.userLogin(userLoginDto);
            if (userDetailsWithProfileDto != null) {
                String token = jwtService.generateToken(userDetailsWithProfileDto.getEmailId());
                return ResponseEntity.ok(new ApiResponseDto<>(true, userDetailsWithProfileDto, null, HttpStatus.OK.value(), token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponseDto<>(false, null, "Invalid email or password", HttpStatus.UNAUTHORIZED.value(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/user/details/{emailId}")
    public ResponseEntity<?> getUserDetails(@PathVariable String emailId) {
        try {
            UserDetailsWithProfileDto userDetailsWithProfileDto = userService.getUserDetailsByEmail(emailId);
            if (userDetailsWithProfileDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponseDto<>(false, null, "User not found.", HttpStatus.NOT_FOUND.value(), null));
            }
            return ResponseEntity.ok(new ApiResponseDto<>(true, userDetailsWithProfileDto, null, HttpStatus.OK.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @PutMapping("/user/update/{emailId}")
    public ResponseEntity<?> updateUser(
            @PathVariable String emailId,
            @RequestPart("userDetails") UserDetailsDto updatedUserDetailsDto,
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic) {

        try {
            String result = userService.updateUser(emailId, updatedUserDetailsDto, profilePic);
            return ResponseEntity.ok(new ApiResponseDto<>(true, result, null, HttpStatus.OK.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }
}
