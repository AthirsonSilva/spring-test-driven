package test.driven.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import test.driven.entity.Student;
import test.driven.repository.StudentRepository;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Log4j2
class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Given a student, when create, then return the student")
    public void create() throws Exception {
        // given - precondition
        Student student = createStudent();

        // when - action
        ResultActions response = mockMvc.perform(
                post("/api/v1/students")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student))
        );

        // then - assertion
        response
                .andExpect((status().isCreated()))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(student.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(student.getLastName()))
                .andExpect(jsonPath("$.email").value(student.getEmail()));
    }

    @Test
    @DisplayName("Given multiple students, when findAll, then return all students")
    public void findAll() throws Exception {
        // given - precondition
        for (int i = 0; i < 2; i++) {
            studentRepository.save(createStudent());
        }

        // when - action
        ResultActions response = mockMvc.perform(
                get("/api/v1/students")
                        .contentType(APPLICATION_JSON));

        // then - assertion
        response
                .andExpect((status().isOk()))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].email").isNotEmpty())
                .andExpect(jsonPath("$[1].email").isNotEmpty());
    }

    private Student createStudent() {
        Faker faker = new Faker();

        return studentRepository.save(
                Student.builder()
                        .firstName(faker.name().firstName())
                        .lastName(faker.name().lastName())
                        .email(faker.internet().emailAddress())
                        .build()
        );
    }
}