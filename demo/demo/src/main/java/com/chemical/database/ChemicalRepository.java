package com.chemical.database;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChemicalRepository extends JpaRepository<Chemical, Long> {
    Optional<Chemical> findByName(String name);
}
