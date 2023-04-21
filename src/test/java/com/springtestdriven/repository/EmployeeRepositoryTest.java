package com.springtestdriven.repository;

import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import lombok.extern.log4j.Log4j2;
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
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private Faker faker;

    @Test
    void test() {
        // given
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .build();

        // when
        employeeRepository.save(employeeEntity);

        // then
        assertNotNull(employeeEntity.getId());

        log.info("employeeEntity = {}", employeeEntity);
    }
}