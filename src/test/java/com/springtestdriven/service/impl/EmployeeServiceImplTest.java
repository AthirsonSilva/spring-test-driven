package com.springtestdriven.service.impl;

import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.exception.EmailAlreadyExistsException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Log4j2
class EmployeeServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private EmployeeServiceImpl employeeService;
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

    @Test
    @DisplayName("Given an invalid employee object, when saving, then an exception is thrown")
    public void saveThrowsException() {
        // given - precondition
        given(employeeRepository.existsByEmail(defaultEmployee.getEmail())).willReturn(true);

        // when - action
        assertThrows(EmailAlreadyExistsException.class, () -> employeeService.save(defaultEmployee));

        // then - assertion
        verify(employeeRepository, never()).save(any(EmployeeEntity.class));
    }

    @Test
    @DisplayName("Given a list of employees in the database, when findAll method is called, then return the list of employees")
    public void findAll() {
        // given - precondition
        EmployeeEntity secondEmployee = createEmployeeEntity();
        given(employeeRepository.findAll()).willReturn(List.of(defaultEmployee, secondEmployee));

        // when - action
        List<EmployeeEntity> employeeEntities = employeeService.findAll();

        // then - assertion
        assertNotNull(employeeEntities);
        assertEquals(2, employeeEntities.size());
    }

    @Test
    @DisplayName("Given a empty list of employees in the database, when findAll method is called, " +
            "then return the list of employees (negative scenario)")
    public void findAllThrows() {
        // given - precondition
        given(employeeRepository.findAll()).willReturn(Collections.emptyList());

        // when - action
        List<EmployeeEntity> employeeEntities = employeeService.findAll();

        // then - assertion
        assertNotNull(employeeEntities);
        assertEquals(0, employeeEntities.size());
    }

    @Test
    @DisplayName("Given an employee in the database, when findById method is called, then return the employee")
    public void findById() {
        // given - precondition
        given(employeeRepository.findById(defaultEmployee.getId())).willReturn(Optional.of(defaultEmployee));

        // when - action
        Optional<EmployeeEntity> employeeEntity = employeeService.findById(defaultEmployee.getId());

        // then - assertion
        assertTrue(employeeEntity.isPresent());
        assertEquals(defaultEmployee.getFirstName(), employeeEntity.get().getFirstName());
    }

    @Test
    @DisplayName("Given an employee in the database, and an employee object, when update method is called, " +
            "then update the employee in the database")
    public void update() {
        // given - precondition
        given(employeeRepository.findById(defaultEmployee.getId())).willReturn(Optional.of(defaultEmployee));
        given(employeeRepository.save(defaultEmployee)).willReturn(defaultEmployee);

        defaultEmployee.setFirstName("Jane");
        defaultEmployee.setEmail(String.format("%s@%s.com", "jane.doe", "gmail"));

        // when - action
        EmployeeEntity updatedEmployee = employeeService.update(defaultEmployee, defaultEmployee.getId());

        // then - assertion
        assertNotNull(updatedEmployee);
        assertEquals(defaultEmployee.getFirstName(), updatedEmployee.getFirstName());
        assertEquals(defaultEmployee.getEmail(), updatedEmployee.getEmail());
    }

    @Test
    @DisplayName("Given an employee in the database, when delete method is called, then delete the employee")
    public void delete() {
        // given - precondition
        willDoNothing().given(employeeRepository).deleteById(defaultEmployee.getId());

        // when - action
        employeeService.delete(defaultEmployee.getId());

        // then - assertion
        verify(employeeRepository).deleteById(defaultEmployee.getId());
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
        Faker faker = new Faker();

        return EmployeeEntity.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();
    }
}