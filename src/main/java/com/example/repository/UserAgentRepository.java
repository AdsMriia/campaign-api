package com.example.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.UserAgent;

@Repository
public interface UserAgentRepository extends JpaRepository<UserAgent, UUID> {
} 