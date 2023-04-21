package com.springtestdriven.repository;

import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Log4j2
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private Faker faker;
    private EmployeeEntity defaultEmployee;

    @BeforeEach
    public void setUp() {
        defaultEmployee = EmployeeEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email(String.format("%s@%s.com", "john.doe", "gmail"))
                .build();
    }

    @AfterEach
    public void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Given employee object, when save is called, then save employee object")
    public void create() {
        // when - action
        EmployeeEntity savedEmployee = employeeRepository.save(defaultEmployee);

        // then - assertion
        assertNotNull(savedEmployee.getId());
        assertEquals(defaultEmployee.getFirstName(), savedEmployee.getFirstName());

        log.info("defaultEmployee = {}", defaultEmployee);
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
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

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
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        EmployeeEntity foundEmployee = employeeRepository.findByEmail(savedEmployee.getEmail()).orElse(null);

        // then - assertion
        assertNotNull(foundEmployee);
        assertEquals(savedEmployee.getId(), foundEmployee.getId());
        assertEquals(savedEmployee.getEmail(), foundEmployee.getEmail());

        log.info("foundEmployee = {}", foundEmployee);
    }

    @Test
    @DisplayName("Given an employee ID and employee object, when update is called, then update employee object")
    public void update() {
        // given - precondition
        EmployeeEntity sampleEmployee = createEmployeeEntity();
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        savedEmployee.setFirstName(sampleEmployee.getFirstName());
        savedEmployee.setLastName(sampleEmployee.getLastName());
        savedEmployee.setEmail(sampleEmployee.getEmail());
        EmployeeEntity updatedEmployee = employeeRepository.save(savedEmployee);

        // then - assertion
        assertNotNull(updatedEmployee);
        assertEquals(savedEmployee.getId(), updatedEmployee.getId());
        assertEquals(sampleEmployee.getFirstName(), updatedEmployee.getFirstName());

        log.info("updatedEmployee = {}", updatedEmployee);
    }

    @Test
    @DisplayName("Given an employee object, when delete is called, then delete employee")
    public void delete() {
        // given - precondition
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        employeeRepository.deleteById(savedEmployee.getId());
        Optional<EmployeeEntity> deletedEmployee = employeeRepository.findById(savedEmployee.getId());

        // then - assertion
        assertTrue(deletedEmployee.isEmpty());
    }

    @Test
    @DisplayName("Given an employee ID, when deleteByID is called, then delete employee")
    public void deleteByID() {
        // given - precondition
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        employeeRepository.delete(savedEmployee);
        Optional<EmployeeEntity> deletedEmployee = employeeRepository.findById(savedEmployee.getId());

        // then - assertion
        assertTrue(deletedEmployee.isEmpty());
    }

    @Test
    @DisplayName("Given an employee first name and last name, when findByFullName is called, then return employee object")
    public void findByFullName() {
        // given - precondition
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        EmployeeEntity foundEmployee = employeeRepository.findByFullName(savedEmployee.getFirstName(), savedEmployee.getLastName()).orElse(null);

        // then - assertion
        assertNotNull(foundEmployee);
        assertEquals(savedEmployee.getId(), foundEmployee.getId());

        log.info("foundEmployee = {}", foundEmployee);
    }

    @Test
    @DisplayName("Given an employee first name or last name, when searchByNames is called, then return list of employee objects")
    public void searchByNames() {
        // given - precondition
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        Iterable<EmployeeEntity> foundEmployees = employeeRepository.searchByNames(savedEmployee.getFirstName());

        // then - assertion
        assertNotNull(foundEmployees);
    }

    @Test
    @DisplayName("Given an range of employee IDs, when findByRangedIDs is called, then return list of employee objects")
    public void findByRangedIDs() {
        // given - precondition
        for (int i = 0; i < 5; i++) {
            employeeRepository.save(createEmployeeEntity());
        }

        // when - action
        Iterable<EmployeeEntity> foundEmployees = employeeRepository.findByRangedIDs(1L, 5L);

        // then - assertion
        assertNotNull(foundEmployees);
        assertEquals(5L, foundEmployees.spliterator().getExactSizeIfKnown());
        foundEmployees.forEach(employeeEntity -> {
            assertNotNull(employeeEntity.getId());
            assertNotNull(employeeEntity.getCreatedAt());
            assertNull(employeeEntity.getUpdatedAt());

            log.info("employeeEntity = {}", employeeEntity);
        });
    }

    @Test
    @DisplayName("Given an employee email address, when findByEmailContaining is called, then return list of employee objects with matching email address")
    public void findByEmailContaining() {
        // given - precondition
        for (int i = 0; i < 5; i++) {
            EmployeeEntity employeeEntity = createEmployeeEntity();
            employeeEntity.setEmail(faker.internet().emailAddress(
                    String.format("%s.%s@email.com", employeeEntity.getFirstName(), employeeEntity.getLastName())
            ));

            employeeRepository.save(employeeEntity);
        }

        // when - action
        Iterable<EmployeeEntity> foundEmployees = employeeRepository.findByEmailContaining("@email.com");

        // then - assertion
        assertNotNull(foundEmployees);
        assertEquals(5L, foundEmployees.spliterator().getExactSizeIfKnown());
        foundEmployees.forEach(employeeEntity -> {
            assertNotNull(employeeEntity.getId());
            assertNotNull(employeeEntity.getCreatedAt());
            assertNull(employeeEntity.getUpdatedAt());

            log.info("employeeEntity = {}", employeeEntity);
        });
    }

    @Test
    @DisplayName("Given an employee email address, when existsByEmail is called, then return true if employee exists")
    public void existsByEmail() {
        // given - precondition
        EmployeeEntity savedEmployee = createAndSaveEmployeeEntity();

        // when - action
        boolean exists = employeeRepository.existsByEmail(savedEmployee.getEmail());

        // then - assertion
        assertTrue(exists);
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