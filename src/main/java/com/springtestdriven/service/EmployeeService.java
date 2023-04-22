package com.springtestdriven.service;

import com.springtestdriven.entity.EmployeeEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeEntity save(EmployeeEntity employee);
    List<EmployeeEntity> findAll();
    Optional<EmployeeEntity> findById(Long id);
    EmployeeEntity update(EmployeeEntity employee, Long id);
    void delete(Long id);
}
