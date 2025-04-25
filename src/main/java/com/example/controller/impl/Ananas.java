//package com.example.controller.impl;
//
//import com.example.model.dto.WebUserDto;
//import com.example.security.jwt.JwtService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.UUID;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/ananas")
//public class Ananas {
//
//    private final JwtService jwtService;
//
//    @GetMapping
//    public String get() {
//
//        WebUserDto webUserDto = new WebUserDto();
//        webUserDto.setId(UUID.randomUUID());
//        webUserDto.setEmail("ananas@ananas");
//        webUserDto.setRoles(new ArrayList<>());
//        webUserDto.setWorkspaceId(UUID.randomUUID());
//
//        return jwtService.generateToken(webUserDto);
//    }
//
//}
