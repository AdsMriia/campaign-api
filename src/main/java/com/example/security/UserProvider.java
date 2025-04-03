package com.example.security;

import com.example.model.dto.WebUserDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserProvider {

    public WebUserDto getCurrentUser() {
        return ((CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal())
                .getWebUserDto();
    }

    public String getCurrentUserToken() {
        return (String) SecurityContextHolder.getContext()
                .getAuthentication()
                .getCredentials();
    }
}
