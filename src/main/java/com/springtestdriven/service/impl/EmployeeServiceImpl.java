package com.springtestdriven.service.impl;

import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.exception.EmailAlreadyExistsException;
import com.springtestdriven.exception.ResourceNotFoundException;
import com.springtestdriven.repository.EmployeeRepository;
import com.springtestdriven.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeEntity> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting employee: {}", id);

        employeeRepository.deleteById(id);
    }

    @Override
    public EmployeeEntity update(EmployeeEntity employee, Long id) {
        log.info("Updating employee: {}", employee);

        EmployeeEntity foundEmployee = findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found: " + id));

        if (!foundEmployee.getEmail().equals(employee.getEmail()) && employeeRepository.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Optional<EmployeeEntity> findById(Long id) {
        Optional<EmployeeEntity> foundEmployee = employeeRepository.findById(id);

        if (foundEmployee.isEmpty()) {
            log.warn("Employee not found: {}", id);

            throw new ResourceNotFoundException("Employee not found: " + id);
        }

        return foundEmployee;
    }

    @Override
    public EmployeeEntity save(EmployeeEntity employee) {
        log.info("Saving employee: {}", employee);

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }
}
