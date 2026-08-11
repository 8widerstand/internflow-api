package com.internflow.api.internship;

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
@AutoConfigureMockMvc
@ActiveProfiles("test") // uses application-test.properties
class InternshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InternshipRepository internshipRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        internshipRepository.deleteAll();
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
                .andExpect(jsonPath("$[0].title").value("Java Internship"));
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
}