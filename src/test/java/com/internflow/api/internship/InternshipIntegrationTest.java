package com.internflow.api.internship;

import com.internflow.api.student.StudentRepository;
import com.internflow.api.student.StudentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // uses application-test.properties
class InternshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        internshipRepository.deleteAll();
        studentRepository.deleteAll();
    }

    @Test
    void createInternshipShouldPersistInternship() throws Exception {
        String requestBody = """
                {
                  "title": "Java Internship",
                  "company": "BMW",
                  "durationInMonths": 6
                }
                """;

        mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));
    }

    @Test
    void createdInternshipShouldBeReturnedInList() throws Exception {
        String requestBody = """
                {
                  "title": "Java Internship",
                  "company": "BMW",
                  "durationInMonths": 6
                }
                """;

        mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/internships")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Java Internship"));
    }

    @Test
    void createdInternshipShouldBeReturnedById() throws Exception {
        String requestBody = """
                {
                  "title": "Java Internship",
                  "company": "BMW",
                  "durationInMonths": 6
                }
                """;

        String responseBody = mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        InternshipResponse created = objectMapper.readValue(responseBody, InternshipResponse.class);


        mockMvc.perform(get("/internships/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));

    }

    @Test
    void updatedStatusShouldBePersisted() throws Exception {

        String statusBody = """
                        {
                          "status": "COMPLETED"
                        }
                """;

        String requestBody = """
                {
                  "title": "Java Internship",
                  "company": "BMW",
                  "durationInMonths": 6
                }
                """;

        String responseBody = mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        InternshipResponse created = objectMapper.readValue(responseBody, InternshipResponse.class);

        mockMvc.perform(patch("/internships/" + created.id() + "/status").contentType(MediaType.APPLICATION_JSON).content(statusBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InternshipStatus.COMPLETED.name()));

        mockMvc.perform(get("/internships/" + created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InternshipStatus.COMPLETED.name()));
    }

    @Test
    void getInternshipsShouldFilterByStatus() throws Exception {
        String statusBody = """
                {
                  "status": "COMPLETED"
                }
                """;

        String requestBody1 = internshipRequestJson("Open Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Completed Internship", "BMW", 6);

        internshipResponseBody(requestBody1);
        String responseBody2 = internshipResponseBody(requestBody2);

        InternshipResponse created2 = objectMapper.readValue(responseBody2, InternshipResponse.class);

        mockMvc.perform(patch("/internships/" + created2.id() + "/status").contentType(MediaType.APPLICATION_JSON).content(statusBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InternshipStatus.COMPLETED.name()));

        mockMvc.perform(get("/internships?status=COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Completed Internship"))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value(InternshipStatus.COMPLETED.name()));

    }

    @Test
    void getInternshipsShouldFilterByCompany() throws Exception {
        String requestBody1 = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Siemens Internship", "Siemens", 8);

        internshipResponseBody(requestBody1);
        internshipResponseBody(requestBody2);

        mockMvc.perform(get("/internships?company=bm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("BMW Internship"))
                .andExpect(jsonPath("$.content[0].company").value("BMW"))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getInternshipsShouldFilterByStatusAndCompany() throws Exception {
        String statusBody = """
                {
                  "status": "COMPLETED"
                }
                """;

        String requestBody1 = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Siemens Internship", "Siemens", 8);


        String responseBody1 = internshipResponseBody(requestBody1);
        internshipResponseBody(requestBody2);

        InternshipResponse created1 = objectMapper.readValue(responseBody1, InternshipResponse.class);

        mockMvc.perform(patch("/internships/" + created1.id() + "/status").contentType(MediaType.APPLICATION_JSON).content(statusBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/internships?status=COMPLETED&company=bm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value(InternshipStatus.COMPLETED.name()))
                .andExpect(jsonPath("$.content[0].company").value("BMW"))
                .andExpect(jsonPath("$.content.length()").value(1));

    }

    @Test
    void shouldAssignStudentToInternship() throws Exception {
        String requestInternshipBody = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestStudentBody = studentRequestJson("Magne", "Candace", "University of Douala", LocalDate.parse("2000-01-01"));
        String responseInternshipBody = internshipResponseBody(requestInternshipBody);
        String responseStudentBody = studentResponseBody(requestStudentBody);

        InternshipResponse created = objectMapper.readValue(responseInternshipBody, InternshipResponse.class);
        StudentResponse student = objectMapper.readValue(responseStudentBody, StudentResponse.class);

        mockMvc.perform(patch("/internships/" + created.id() + "/student/" + student.id()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(student.id()));
    }

    @Test
    void blankCompanyFilterShouldReturnAllInternships() throws Exception {
        String requestBody1 = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Siemens Internship", "Siemens", 8);

        internshipResponseBody(requestBody1);
        internshipResponseBody(requestBody2);

        mockMvc.perform(get("/internships?company= "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

    }

    @Test
    void getInternshipsShouldReturnBadRequestWhenStatusParameterIsInvalid() throws Exception {
        mockMvc.perform(get("/internships?status=UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.status").value("Invalid internship status"));
    }

    @Test
    void getInternshipsShouldReturnRequestedPage() throws Exception {
        String requestBody1 = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Siemens Internship", "Siemens", 8);
        String requestBody3 = internshipRequestJson("Diehl Group Internship", "Diehl", 7);


        internshipResponseBody(requestBody1);
        internshipResponseBody(requestBody2);
        internshipResponseBody(requestBody3);

        mockMvc.perform(get("/internships?page=1&size=2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void getInternshipsShouldReturnBadRequestWhenPaginationParametersAreInvalid() throws Exception {
        mockMvc.perform(get("/internships?page=-1&size=10")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.page").value("Page must be greater than or equal to 0"));
    }

    @Test
    void getInternshipsShouldReturnInternshipsSortedByCompanyDescending() throws Exception {
        String requestBody1 = internshipRequestJson("BMW Internship", "BMW", 6);
        String requestBody2 = internshipRequestJson("Siemens Internship", "Siemens", 8);
        String requestBody3 = internshipRequestJson("Diehl Group Internship", "Diehl", 7);

        internshipResponseBody(requestBody1);
        internshipResponseBody(requestBody2);
        internshipResponseBody(requestBody3);

        mockMvc.perform(get("/internships?sort=company,desc")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].company").value("Siemens"))
                .andExpect(jsonPath("$.content[1].company").value("Diehl"))
                .andExpect(jsonPath("$.content[2].company").value("BMW"));
    }

    private String internshipResponseBody(String requestBody) throws Exception {
        return mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String studentResponseBody(String requestBody) throws Exception {
        return mockMvc.perform(post("/students").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String internshipRequestJson(
            String title,
            String company,
            Integer durationInMonths
    ) {
        return """
                {
                    "title": "%s",
                    "company": "%s",
                    "durationInMonths": %d
                }
                """.formatted(title, company, durationInMonths);
    }

    private String studentRequestJson(
            String firstName,
            String lastName,
            String university,
            LocalDate birthDate
    ) {
        return """
                        {
                            "firstName": "%s",
                            "lastName": "%s",
                            "university": "%s",
                            "birthDate": "%s"
                        }
                """.formatted(firstName, lastName, university, birthDate);
    }
}
