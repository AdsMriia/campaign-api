package com.example.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.PartnerLinkClick;

@Repository
public interface PartnerLinkClickRepository extends JpaRepository<PartnerLinkClick, UUID> {
}
