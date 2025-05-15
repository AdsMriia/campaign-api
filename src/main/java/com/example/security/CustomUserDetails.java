package com.example.security;

import com.example.model.dto.WebUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Заглушка для пользовательских деталей
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final WebUserDto webUserDto;

    /**
     * Возвращает роли и разрешения пользователя В этой заглушке возвращает
     * пустой набор
     *
     * @return Пустой набор ролей/разрешений
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
         return new ArrayList<>();
//       return webUserDto.getRoles().stream()
//               .map(SimpleGrantedAuthority::new)
//               .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null; // Пароль не используется, так как аутентификация через JWT
    }

    @Override
    public String getUsername() {
        return null;
    }

    /**
     * Получить ID пользователя
     *
     * @return ID пользователя
     */
    public WebUserDto getWebUserDto() {
        return webUserDto;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
