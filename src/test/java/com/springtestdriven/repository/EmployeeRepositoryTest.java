package com.springtestdriven.repository;

import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Log4j2
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Faker faker;

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Given employee object, when save is called, then save employee object")
    void create() {
        // given - precondition
        EmployeeEntity employeeEntity = createEmployeeEntity();

        // when - action
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);

        // then - assertion
        assertNotNull(savedEmployee.getId());
        assertEquals(employeeEntity.getFirstName(), savedEmployee.getFirstName());

        log.info("employeeEntity = {}", employeeEntity);
    }

    @Test
    @DisplayName("Given employees in the database, when findAll is called, then return list of employees")
    public void findAll() {
        // given - precondition
        for (int i = 0; i < 5; i++) {
            employeeRepository.save(createEmployeeEntity());
        }

        // when - action
        Iterable<EmployeeEntity> employeeEntities = employeeRepository.findAll();

        // then - assertion
        assertEquals(5, employeeEntities.spliterator().getExactSizeIfKnown());
        employeeEntities.forEach(employeeEntity -> {
            assertNotNull(employeeEntity.getId());
            assertNotNull(employeeEntity.getCreatedAt());
            assertNull(employeeEntity.getUpdatedAt());
        });

        log.info("employeeEntities = {}", employeeEntities);
    }

    @Test
    @DisplayName("Given an employee ID, when findById is called, then return employee object")
    public void findByID() {
        // given - precondition
        EmployeeEntity employeeEntity = createEmployeeEntity();
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);

        // when - action
        EmployeeEntity foundEmployee = employeeRepository.findById(savedEmployee.getId()).orElse(null);

        // then - assertion
        assertNotNull(foundEmployee);
        assertEquals(savedEmployee.getId(), foundEmployee.getId());
        assertEquals(savedEmployee.getFirstName(), foundEmployee.getFirstName());

        log.info("foundEmployee = {}", foundEmployee);
    }

    @Test
    @DisplayName("Given an employee email address, when findByEmail is called, then return employee object")
    public void findByEmail() {
        // given - precondition
        EmployeeEntity employeeEntity = createEmployeeEntity();
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);

        // when - action
        EmployeeEntity foundEmployee = employeeRepository.findByEmail(savedEmployee.getEmail()).orElse(null);

        // then - assertion
        assertNotNull(foundEmployee);
        assertEquals(savedEmployee.getId(), foundEmployee.getId());
        assertEquals(savedEmployee.getEmail(), foundEmployee.getEmail());

        log.info("foundEmployee = {}", foundEmployee);
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