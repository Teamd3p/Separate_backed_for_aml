package com.tss.aml.repository;

import com.tss.aml.entity.SuspiciousKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuspiciousKeywordRepository extends JpaRepository<SuspiciousKeyword, Long> {
    List<SuspiciousKeyword> findByIsActiveTrue();
}