package test.driven.testcontainers.repository;

import com.github.javafaker.Faker;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;
import test.driven.entity.Student;
import test.driven.repository.StudentRepository;
import test.driven.testcontainers.base.AbstractBaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@DataJpaTest
@Testcontainers
@Log4j2
class StudentRepositoryTest extends AbstractBaseTest {
    @Autowired
    private StudentRepository studentRepository;

    private Student defaultStudent;

    @BeforeEach
    void setUp() {
        defaultStudent = Student.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@gmail.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        studentRepository.deleteAll();
    }

    @Test
    @DisplayName("Given a student, when create, then return the student")
    public void create() {
        // given - precondition
        Student student = studentRepository.save(defaultStudent);

        // when - action
        Student studentFound = studentRepository.findById(student.getId()).orElse(null);

        // then - assertion
        assertNotNull(studentFound);
        assertEquals(student.getId(), studentFound.getId());
    }

    @Test
    @DisplayName("Given multiple students, when findAll, then return all students")
    public void findAll() {
        // given - precondition
        for (int i = 0; i < 2; i++) {
            studentRepository.save(createStudent());
        }

        // when - action
        Iterable<Student> students = studentRepository.findAll();

        // then - assertion
        assertNotNull(students);
        assertEquals(2, students.spliterator().getExactSizeIfKnown());
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