package com.springtestdriven.integration.testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.repository.EmployeeRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Log4j2
public class EmployeeControllerIntegrationTest {
    @Container
    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:latest");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();
    }

    @Test
    @DisplayName("Given a valid employee, when save, then return the employee")
    public void save() throws Exception {
        // given - precondition
        EmployeeEntity employeeEntity = createEmployeeEntity();

        // when - action
        ResultActions response = mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeEntity)));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(employeeEntity.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employeeEntity.getLastName()))
                .andExpect(jsonPath("$.email").value(employeeEntity.getEmail()));
    }

    @Test
    @DisplayName("Given a list of employees, when findAll, then return the list of employees")
    public void findAll() throws Exception {
        // given - precondition
        for (int i = 0; i < 2; i++) {
            employeeRepository.save(createEmployeeEntity());
        }

        // when - action
        ResultActions response = mockMvc.perform(get("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpectAll(
                        jsonPath("$[0].email").isNotEmpty(),
                        jsonPath("$[1].email").isNotEmpty()
                );
    }

    @Test
    @DisplayName("Given a valid employee id, when findById, then return the employee")
    public void findById() throws Exception {
        // given - precondition
        EmployeeEntity employee = employeeRepository.save(createEmployeeEntity());


        // when - action
        ResultActions response = mockMvc.perform(get("/api/v1/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.email").isNotEmpty(),
                        jsonPath("$.firstName").value(employee.getFirstName()),
                        jsonPath("$.lastName").value(employee.getLastName())
                );
    }

    @Test
    @DisplayName("Given a invalid employee id, when findById, then return 404")
    public void findByIdNegative() throws Exception {
        // given - precondition
        employeeRepository.save(createEmployeeEntity());

        // when - action
        ResultActions response = mockMvc.perform(get("/api/v1/employees/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given a valid employee object and id, when update, then return the employee")
    public void update() throws Exception {
        // given - precondition
        EmployeeEntity employeeEntity = employeeRepository.save(createEmployeeEntity());

        // when - action
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", employeeEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeEntity)));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value(employeeEntity.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(employeeEntity.getLastName()))
                .andExpect(jsonPath("$.email").value(employeeEntity.getEmail()));
    }

    @Test
    @DisplayName("Given a invalid employee object or id, when update, then return 404")
    public void updateNegative() throws Exception {
        // given - precondition
        EmployeeEntity employeeEntity = employeeRepository.save(createEmployeeEntity());

        // when - action
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", 0L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeEntity)));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given a valid employee id, when delete, then return 204")
    public void deleteById() throws Exception {
        // given - precondition
        EmployeeEntity employee = employeeRepository.save(createEmployeeEntity());

        // when - action
        ResultActions response = mockMvc.perform(delete("/api/v1/employees/{id}", employee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then - assertion
        response.andDo(
                        mvcResult -> log.info("HTTP Response --> " + mvcResult.getResponse().getContentAsString())
                )
                .andExpect(status().isNoContent());
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
