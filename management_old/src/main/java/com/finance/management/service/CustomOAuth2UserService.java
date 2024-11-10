package com.finance.management.service;

import com.finance.management.dto.UserDetailsDto;
import com.finance.management.entity.UserDetailsEntity;
import com.finance.management.repository.UserManagementRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserManagementRepository userManagementRepository;

    public CustomOAuth2UserService(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String githubId = oAuth2User.getAttribute("id");
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");



        // Here you can set roles, etc., if necessary

        return oAuth2User; // Return the OAuth2User
    }
}
