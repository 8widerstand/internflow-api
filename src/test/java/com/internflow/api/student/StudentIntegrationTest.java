package com.internflow.api.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class StudentIntegrationTest {


    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldSaveAndFindStudent() {
        Student student = new Student("Magne", "Candace", "University of Douala",
                LocalDate.of(2000, 1, 1));

        Student savedStudent = studentRepository.save(student);
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getFirstName()).isEqualTo("Magne");
        assertThat(foundStudent.get().getLastName()).isEqualTo("Candace");
        assertThat(foundStudent.get().getUniversity()).isEqualTo("University of Douala");
        assertThat(foundStudent.get().getBirthDate()).isEqualTo(LocalDate.of(2000, 1, 1));
    }
}
