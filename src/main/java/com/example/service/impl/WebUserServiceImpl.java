package com.example.service.impl;

import com.example.entity.Media;
import com.example.model.dto.WebUserDto;
import com.example.security.CustomUserDetails;
import com.example.service.WebUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WebUserServiceImpl implements WebUserService {

    @Override
    public UUID getCurrentUserId() {
        return null;
    }

    @Override
    public UUID getCurrentWorkspaceId() {
        return null;
    }

    @Override
    public List<Media> getWorkspaceWithMedia() {
        return List.of();
    }

    @Override
    public WebUserDto getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getWebUserDto();
    }
}
