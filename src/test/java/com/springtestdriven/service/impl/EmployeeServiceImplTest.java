package com.springtestdriven.service.impl;

import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.repository.EmployeeRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@Log4j2
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    private Faker faker;
    private EmployeeEntity defaultEmployee;

    @BeforeEach
    void setUp() {
        defaultEmployee = EmployeeEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email(String.format("%s@%s.com", "john.doe", "gmail"))
                .build();
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Given an employee, when saving, then the employee is saved")
    void save() {
        // given
        log.info("Saving employee: {}", defaultEmployee);

        given(employeeRepository.existsByEmail(defaultEmployee.getEmail())).willReturn(false);
        given(employeeRepository.save(defaultEmployee)).willReturn(defaultEmployee);

        // when
        EmployeeEntity savedEmployee = employeeService.save(defaultEmployee);

        // then
        assertNotNull(savedEmployee);
        assertEquals(defaultEmployee.getFirstName(), savedEmployee.getFirstName());

        log.info("Saved employee: {}", savedEmployee);
    }

    /**
     * @return EmployeeEntity
     * @implNote Create and save EmployeeEntity object with random data using Faker
     */
    private EmployeeEntity createAndSaveEmployeeEntity() {
        EmployeeEntity employeeEntity = createEmployeeEntity();

        return employeeRepository.save(employeeEntity);
    }

    /**
     * @return EmployeeEntity
     * @implNote Create EmployeeEntity object with random data using Faker
     */
    private EmployeeEntity createEmployeeEntity() {
        return EmployeeEntity.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();
    }
}