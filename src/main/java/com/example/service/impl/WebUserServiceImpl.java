package com.example.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.entity.Media;
import com.example.model.dto.WebUserDto;
import com.example.security.CustomUserDetails;
import com.example.service.WebUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebUserServiceImpl implements WebUserService {

    @Override
    public UUID getCurrentUserId() {
        WebUserDto userDto = getCurrentUser();
        return userDto != null ? userDto.getId() : null;
    }

    @Deprecated
    @Override
    public UUID getCurrentWorkspaceId() {
        return getCurrentUser().getWorkspaceId();
    }

    @Override
    public List<Media> getWorkspaceWithMedia() {
        return List.of();
    }

    @Override
    public WebUserDto getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                return userDetails.getWebUserDto();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
