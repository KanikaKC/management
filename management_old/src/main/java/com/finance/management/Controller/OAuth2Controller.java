//package com.finance.management.Controller;
//
//import com.finance.management.dto.ApiResponseDto;
//import com.finance.management.dto.UserDetailsDto;
//import com.finance.management.service.GoogleService;
//import com.finance.management.service.JwtService;
//import com.finance.management.service.UserManagementService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class OAuth2Controller {
//
//    @Autowired
//    private UserManagementService userManagementService;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private GoogleService googleService;
//
//
//    @GetMapping("/oauth2/callback/google")
//    public ResponseEntity<ApiResponseDto<UserDetailsDto>> googleCallback(@RequestParam("code") String code) {
//        try {
//            String accessToken = googleService.exchangeCodeForAccessToken(code);
//
//            UserDetailsDto userDetails = googleService.getUserInfo(accessToken);
//
//            String token = jwtService.generateToken(userDetails.getEmailId());
//
//            return ResponseEntity.ok(new ApiResponseDto<>(true, userDetails, null, HttpStatus.OK.value(), token));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new ApiResponseDto<>(false, null, "User not authenticated", HttpStatus.UNAUTHORIZED.value(), null));
//        }
//    }
//
//}
//
