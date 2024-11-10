//package com.finance.management.service;
//
//import com.finance.management.dto.UserDetailsDto;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.util.Map;
//
//@Service
//public class GoogleService {
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
//    private String redirectUri;
//
//    @Value("${spring.security.oauth2.client.registration.google.scope}")
//    private String email;
//
//    private final RestTemplate restTemplate;
//
//    public GoogleService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public String exchangeCodeForAccessToken(String code) {
//        String tokenUrl = "https://oauth2.googleapis.com/token";
//
//        // Prepare the request body
//        String body = UriComponentsBuilder.fromHttpUrl(tokenUrl)
//                .queryParam("code", code)
//                .queryParam("client_id", clientId)
//                .queryParam("client_secret", clientSecret)
//                .queryParam("redirect_uri", redirectUri)
//                .queryParam("grant_type", "authorization_code")
//                .queryParam("email",email)
//                .toUriString();
//
//        HttpEntity<String> request = new HttpEntity<>(body, new HttpHeaders());
//        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
//                tokenUrl,
//                HttpMethod.POST,
//                request,
//                new ParameterizedTypeReference<Map<String, String>>() {}
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody().get("access_token");
//        } else {
//            throw new RuntimeException("Failed to exchange code for access token");
//        }
//    }
//
//    public UserDetailsDto getUserInfo(String accessToken) {
//        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
//                userInfoUrl,
//                HttpMethod.GET,
//                entity,
//                new ParameterizedTypeReference<Map<String, Object>>() {}
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> userInfo = response.getBody();
//            return new UserDetailsDto(
//                    (String) userInfo.get("name"),
//                    (String) userInfo.get("email"),
//                    null, // No password for OAuth users
//                    null,
//                    null
//            );
//        } else {
//            throw new RuntimeException("Failed to fetch user info");
//        }
//    }
//}
//
