package com.springtestdriven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.springtestdriven.entity.EmployeeEntity;
import com.springtestdriven.service.EmployeeService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Log4j2
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeEntity defaultEmployee;

    @BeforeEach
    void setUp() {
        defaultEmployee = createEmployeeEntity();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Given a valid employee, when save, then return the employee")
    public void save() throws Exception {
        // given - precondition
        EmployeeEntity employeeEntity = createEmployeeEntity();
        given(employeeService.save(ArgumentMatchers.any(EmployeeEntity.class))).willAnswer(
                invocation -> invocation.getArgument(0)
        );

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
        given(employeeService.findAll()).willReturn(List.of(
                createEmployeeEntity(),
                createEmployeeEntity()
        ));

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
        given(employeeService.findById(ArgumentMatchers.anyLong()))
                .willReturn(Optional.of(defaultEmployee));

        // when - action
        ResultActions response = mockMvc.perform(get("/api/v1/employees/{id}", 1L)
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
                        jsonPath("$.firstName").value(defaultEmployee.getFirstName()),
                        jsonPath("$.lastName").value(defaultEmployee.getLastName())
                );
    }

    @Test
    @DisplayName("Given a invalid employee id, when findById, then return 404")
    public void findByIdNegative() throws Exception {
        // given - precondition
        given(employeeService.findById(ArgumentMatchers.anyLong()))
                .willReturn(Optional.empty());

        // when - action
        ResultActions response = mockMvc.perform(get("/api/v1/employees/{id}", 1L)
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
        EmployeeEntity employeeEntity = createEmployeeEntity();

        given(employeeService.findById(ArgumentMatchers.anyLong()))
                .willReturn(Optional.of(defaultEmployee));
        given(employeeService.update(ArgumentMatchers.any(EmployeeEntity.class), ArgumentMatchers.anyLong()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when - action
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", 1L)
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
        EmployeeEntity employeeEntity = createEmployeeEntity();
        given(employeeService.findById(ArgumentMatchers.anyLong()))
                .willReturn(Optional.empty());

        // when - action
        ResultActions response = mockMvc.perform(put("/api/v1/employees/{id}", 1L)
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
        given(employeeService.findById(ArgumentMatchers.anyLong()))
                .willReturn(Optional.of(defaultEmployee));
        willDoNothing().given(employeeService).delete(ArgumentMatchers.anyLong());

        // when - action
        ResultActions response = mockMvc.perform(delete("/api/v1/employees/{id}", 1L));

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