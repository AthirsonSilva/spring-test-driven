package com.springtestdriven.repository;

import com.springtestdriven.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    @Query("SELECT e FROM EmployeeEntity e WHERE e.email = ?1")
    Optional<EmployeeEntity> findByEmail(String email);
}
