package com.tss.aml.repository;

import com.tss.aml.entity.RiskyCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskyCountryRepository extends JpaRepository<RiskyCountry, String> {
}