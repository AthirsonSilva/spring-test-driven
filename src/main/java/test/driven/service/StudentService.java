package test.driven.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import test.driven.entity.Student;
import test.driven.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class StudentService {
    private final StudentRepository studentRepository;

    public Student create(Student student) {
        log.info("Saving student: {}", student);
        return studentRepository.save(student);
    }

    public List<Student> findAll() {
        log.info("Finding all students");
        return studentRepository.findAll();
    }
}
