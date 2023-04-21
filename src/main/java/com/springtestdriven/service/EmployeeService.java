package com.springtestdriven.service;

import com.springtestdriven.entity.EmployeeEntity;
import org.springframework.stereotype.Service;

public interface EmployeeService {
    EmployeeEntity save(EmployeeEntity employee);
}
