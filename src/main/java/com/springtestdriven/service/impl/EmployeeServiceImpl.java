package com.springtestdriven.service.impl;

import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.exception.EmailAlreadyExistsException;
import com.springtestdriven.repository.EmployeeRepository;
import com.springtestdriven.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeEntity save(EmployeeEntity employee) {
        log.info("Saving employee: {}", employee);

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }
}
