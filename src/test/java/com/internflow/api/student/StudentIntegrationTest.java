package com.internflow.api.student;

import com.internflow.api.internship.Internship;
import com.internflow.api.internship.InternshipRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class StudentIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InternshipRepository internshipRepository;

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

    @Test
    void shouldSaveInternshipWithAssignedStudent() {
        Student student = new Student("Magne", "Candace", "University of Douala",
                LocalDate.of(2000, 1, 1));
        Student savedStudent = studentRepository.save(student);

        Internship internship = new Internship("Backend internship", "BMW Group", 6);
        internship.assignStudent(savedStudent);
        Internship savedInternship = internshipRepository.save(internship);

        Optional<Internship> foundInternship = internshipRepository.findById(savedInternship.getId());
        assertThat(foundInternship).isPresent();
        assertThat(foundInternship.get().getStudent()).isNotNull();
        assertThat(foundInternship.get().getStudent().getId()).isEqualTo(savedStudent.getId());
        assertThat(foundInternship.get().getStudent().getFirstName()).isEqualTo("Magne");
    }

    @Test
    void createStudentShouldPersistStudent() throws Exception {
        String requestBody = """
                {
                  "firstName": "Magne",
                  "lastName": "Candace",
                  "university": "University of Douala",
                  "birthDate": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Magne"))
                .andExpect(jsonPath("$.lastName").value("Candace"))
                .andExpect(jsonPath("$.university").value("University of Douala"))
                .andExpect(jsonPath("$.birthDate").value("2000-01-01"));

    }
}
