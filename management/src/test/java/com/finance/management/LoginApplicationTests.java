package com.finance.management;

import com.finance.management.Controller.UserController;
import com.finance.management.dto.ApiResponseDto;
import com.finance.management.dto.UserDetailsDto;
import com.finance.management.dto.UserDetailsWithProfileDto;
import com.finance.management.dto.UserLoginDto;
import com.finance.management.service.JwtService;
import com.finance.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoginApplicationTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldReturnCreatedResponse() {
        // Arrange
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        userDetailsDto.setEmailId("test@example.com");
        userDetailsDto.setUsername("testUser");
        userDetailsDto.setPassword("securePassword");
        userDetailsDto.setPhoneNumber(1234567890);
        userDetailsDto.setAddress("Test Address");

        // Mocking the userService method
        when(userService.createUser(any(UserDetailsDto.class))).thenReturn("User Signup successfully");

        // Act
        ResponseEntity<ApiResponseDto<String>> response = userController.createUser(userDetailsDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        ApiResponseDto<String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("User Signup successfully", responseBody.getData());  // Check for 'message'
    }


    @Test
    void createUser_ShouldReturnBadRequest_OnIllegalArgumentException() {
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        when(userService.createUser(any(UserDetailsDto.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = userController.createUser(userDetailsDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponseDto<?> responseBody = (ApiResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Invalid data", responseBody.getError());
    }

    @Test
    void userLogin_ShouldReturnOkResponse_WhenLoginSuccessful() {
        UserLoginDto userLoginDto = new UserLoginDto("test@example.com", "password");
        UserDetailsWithProfileDto userDetails = new UserDetailsWithProfileDto();
        userDetails.setEmailId("test@example.com");

        when(userService.userLogin(any(UserLoginDto.class))).thenReturn(userDetails);
        when(jwtService.generateToken(anyString())).thenReturn("jwt-token");

        ResponseEntity<ApiResponseDto<UserDetailsWithProfileDto>> response = userController.userLogin(userLoginDto);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponseDto<UserDetailsWithProfileDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals("jwt-token", responseBody.getToken());
    }

    @Test
    void userLogin_ShouldReturnUnauthorizedResponse_WhenLoginFails() {
        UserLoginDto userLoginDto = new UserLoginDto("test@example.com", "wrong-password");

        when(userService.userLogin(any(UserLoginDto.class))).thenReturn(null);

        ResponseEntity<ApiResponseDto<UserDetailsWithProfileDto>> response = userController.userLogin(userLoginDto);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ApiResponseDto<UserDetailsWithProfileDto> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Invalid email or password", responseBody.getError());
    }

    @Test
    void getUserDetails_ShouldReturnOkResponse_WhenUserExists() {
        String emailId = "test@example.com";
        UserDetailsWithProfileDto userDetails = new UserDetailsWithProfileDto();
        userDetails.setEmailId(emailId);

        when(userService.getUserDetailsByEmail(emailId)).thenReturn(userDetails);

        ResponseEntity<?> response = userController.getUserDetails(emailId);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponseDto<?> responseBody = (ApiResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isSuccess());
        assertEquals(userDetails, responseBody.getData());
    }

    @Test
    void getUserDetails_ShouldReturnNotFoundResponse_WhenUserDoesNotExist() {
        String emailId = "unknown@example.com";
        when(userService.getUserDetailsByEmail(emailId)).thenReturn(null);

        ResponseEntity<?> response = userController.getUserDetails(emailId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ApiResponseDto<?> responseBody = (ApiResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("User not found.", responseBody.getError());
    }

    @Test
    void updateUser_ShouldReturnBadRequest_OnUserNotFoundException() throws Exception {
        String emailId = "test@example.com";
        UserDetailsDto updatedUserDetailsDto = new UserDetailsDto();
        updatedUserDetailsDto.setUsername("newUsername");
        updatedUserDetailsDto.setPhoneNumber(1234567890);
        updatedUserDetailsDto.setAddress("newAddress");
        updatedUserDetailsDto.setEmailId(emailId);

        when(userService.updateUser(eq(emailId), any(UserDetailsDto.class), any())).thenThrow(new IllegalArgumentException("User not found"));

        ResponseEntity<?> response = userController.updateUser(emailId, updatedUserDetailsDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponseDto<?> responseBody = (ApiResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("User not found", responseBody.getError());
    }

    @Test
    void updateUser_ShouldReturnBadRequest_OnIllegalArgumentException() throws Exception {
        String emailId = "test@example.com";
        UserDetailsDto updatedUserDetailsDto = new UserDetailsDto();
        when(userService.updateUser(eq(emailId), any(UserDetailsDto.class), any())).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = userController.updateUser(emailId, updatedUserDetailsDto, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponseDto<?> responseBody = (ApiResponseDto<?>) response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isSuccess());
        assertEquals("Invalid data", responseBody.getError());
    }

}
