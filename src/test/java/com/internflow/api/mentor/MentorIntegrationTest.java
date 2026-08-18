package com.internflow.api.mentor;

import com.internflow.api.internship.InternshipRepository;
import com.internflow.api.internship.InternshipResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MentorIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private MentorRepository mentorRepository;


    @BeforeEach
    void cleanDatabase() {
        internshipRepository.deleteAll();
        mentorRepository.deleteAll();
    }

    @Test
    void createMentorShouldPersistMentor() throws Exception {
        String requestBody = mentorRequestJson("Ada", "Bienvenue", "ada@example.com");
        mockMvc.perform(post("/mentors").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.lastName").value("Bienvenue"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void getAllMentorsShouldReturnMentors() throws Exception {
        String requestBody = mentorRequestJson("Ada", "Bienvenue", "ada@example.com");
        mentorResponseBody(requestBody);

        mockMvc.perform(get("/mentors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Ada"))
                .andExpect(jsonPath("$[0].lastName").value("Bienvenue"))
                .andExpect(jsonPath("$[0].email").value("ada@example.com"));
    }

    @Test
    void getMentorByIdShouldReturnMentor() throws Exception {
        String requestBody = mentorRequestJson("Ada", "Bienvenue", "ada@example.com");
        String mentorResponse = mentorResponseBody(requestBody);
        MentorResponse createdMentor = objectMapper.readValue(mentorResponse, MentorResponse.class);

        mockMvc.perform(get("/mentors/" + createdMentor.id())).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.lastName").value("Bienvenue"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));
    }

    @Test
    void getMentorByIdShouldReturnNotFoundWhenMentorDoesNotExist() throws Exception {
        mockMvc.perform(get("/mentors/1")).
                andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.errors.resource").exists());
    }

    @Test
    void assignMentorToInternshipShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        String requestBody = mentorRequestJson("Ada", "Bienvenue", "ada@example.com");
        String mentorResponse = mentorResponseBody(requestBody);
        MentorResponse createdMentor = objectMapper.readValue(mentorResponse, MentorResponse.class);

        mockMvc.perform(patch("/internships/1/mentor/" + createdMentor.id())).
                andExpect(status().isNotFound());
    }

    @Test
    void assignMentorToInternshipShouldReturnNotFoundWhenMentorDoesNotExist() throws Exception {
        String requestInternshipBody = internshipRequestJson("Java Internship", "BMW", 6);
        String internshipResponse = internshipResponseBody(requestInternshipBody);
        InternshipResponse createdInternship = objectMapper.readValue(internshipResponse, InternshipResponse.class);

        mockMvc.perform(patch("/internships/" + createdInternship.id() + "/mentor/1"))
                .andExpect(status().isNotFound());
    }

    private String mentorResponseBody(String requestBody) throws Exception {
        return mockMvc.perform(post("/mentors").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String internshipResponseBody(String requestBody) throws Exception {
        return mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private String mentorRequestJson(
            String firstName,
            String lastName,
            String email
    ) {
        return """
                        {
                            "firstName": "%s",
                            "lastName": "%s",
                            "email": "%s"
                        }
                """.formatted(firstName, lastName, email);
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
}
