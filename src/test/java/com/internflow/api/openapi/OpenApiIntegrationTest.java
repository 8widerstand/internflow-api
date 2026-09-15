package com.internflow.api.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocsShouldExposeInternshipOperations() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.openapi").exists())
            .andExpect(jsonPath("$.info.title").value("InternFlow API"))
            .andExpect(jsonPath("$.info.version").value("v1"))
            .andExpect(jsonPath("$.info.description").value("REST API for managing internships, students, mentors and tasks"))
            .andExpect(jsonPath("$.paths['/internships'].get").exists())
            .andExpect(jsonPath("$.paths['/internships'].get.tags[0]").value("Internships"))
            .andExpect(jsonPath("$.paths['/internships'].post").exists())
            .andExpect(jsonPath("$.paths['/internships'].post.summary").value("Create an internship"))
            .andExpect(jsonPath("$.paths['/internships'].post.responses['201']").exists())
            .andExpect(jsonPath("$.paths['/internships'].post.responses['400']").exists())
            .andExpect(jsonPath("$.paths['/internships'].post.responses['400'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/ApiErrorResponse"));
    }

    @Test
    void openApiDocsShouldExposeStudentOperations() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/students'].get").exists())
                .andExpect(jsonPath("$.paths['/students'].get.tags[0]").value("Students"))
                .andExpect(jsonPath("$.paths['/students'].post").exists())
                .andExpect(jsonPath("$.paths['/students'].post.summary").value("Create a student"))
                .andExpect(jsonPath("$.paths['/students'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/students'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/students'].post.responses['400'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/ApiErrorResponse"));

    }

    @Test
    void openApiDocsShouldExposeMentorOperations()  throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/mentors'].get").exists())
                .andExpect(jsonPath("$.paths['/mentors'].get.tags[0]").value("Mentors"))
                .andExpect(jsonPath("$.paths['/mentors'].post").exists())
                .andExpect(jsonPath("$.paths['/mentors'].post.summary").value("Create a mentor"))
                .andExpect(jsonPath("$.paths['/mentors'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/mentors'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/mentors'].post.responses['400'].content['application/json'].schema['$ref']")
                        .value("#/components/schemas/ApiErrorResponse"));
    }

    @Test
    void openApiDocsShouldExposeTaskOperations()   throws Exception {


        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].get").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].get.tags[0]").value("Tasks"))
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.summary").value("Create a task for an internship"))
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.responses['400']" +
                        ".content['application/json'].schema['$ref']").value("#/components/schemas/ApiErrorResponse"))
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/internships/{internshipId}/tasks'].post.responses['404']" +
                        ".content['application/json'].schema['$ref']").value("#/components/schemas/ApiErrorResponse"));
    }

    @Test
    void openApiDocsShouldExposeHealthOperation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.openapi").exists())
                .andExpect(jsonPath("$.paths['/health'].get").exists())
                .andExpect(jsonPath("$.paths['/health'].get.tags[0]").value("Health"))
                .andExpect(jsonPath("$.paths['/health'].get.summary").value("Check API health"));
    }
}

