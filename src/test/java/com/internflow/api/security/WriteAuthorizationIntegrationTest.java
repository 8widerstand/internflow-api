package com.internflow.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WriteAuthorizationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private static final String INTERNSHIP_JSON = """
            {
                "title": "Java Internship",
                "company": "BMW",
                "durationInMonths": 6
            }
            """;

    @Test
    void anonymousWriteShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/internships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(INTERNSHIP_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedWriteShouldSuccess() throws Exception {
        mockMvc.perform(post("/internships")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(INTERNSHIP_JSON))
                .andExpect(status().isCreated());
    }
}
