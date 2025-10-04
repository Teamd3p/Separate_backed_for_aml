package com.tss.aml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.aml.entity.Rule;

public interface RuleRepository extends JpaRepository<Rule, Long> {
    List<Rule> findByIsActiveTrue();
}