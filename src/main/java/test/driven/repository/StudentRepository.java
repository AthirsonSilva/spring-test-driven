package test.driven.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.driven.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
