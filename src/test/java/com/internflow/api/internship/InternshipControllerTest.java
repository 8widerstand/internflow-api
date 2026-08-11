package com.internflow.api.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternshipController.class)
public class InternshipControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InternshipService internshipService;

    @Test
    void getInternshipByIdShouldReturnOkWhenInternshipExists() throws Exception {
        InternshipResponse response = new InternshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);

        when(internshipService.findInternshipById(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/internships/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));
    }

    @Test
    void getInternshipByIdShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        when(internshipService.findInternshipById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/internships/1")).andExpect(status().isNotFound());
    }

    @Test
    void getAllInternshipsShouldReturnOkWithInternships() throws Exception {
        InternshipResponse internship1 = new InternshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        InternshipResponse internship2 = new InternshipResponse(2L, "Backend Internship", "Siemens", 8, InternshipStatus.OPEN);

        when(internshipService.findAllInternships()).thenReturn(List.of(internship1, internship2));

        mockMvc.perform(get("/internships")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Java Internship"))
                .andExpect(jsonPath("$[1].title").value("Backend Internship"));
    }

    @Test
    void createInternshipShouldReturnCreatedWhenRequestIsValid() throws Exception {
        InternshipResponse response = new InternshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        String requestBody = """
                {
                  "title": "Java Internship",
                  "company": "BMW",
                  "durationInMonths": 6
                }
                """;
        when(internshipService.create(any(CreateInternshipRequest.class))).thenReturn(response);
        mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/internships/1"))
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));
    }

    @Test
    void createInternshipShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                    "title": "",
                    "company": "",
                    "durationInMonths": 0
                }
                """;

        mockMvc.perform(post("/internships").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.company").value("Company is required"))
                .andExpect(jsonPath("$.errors.durationInMonths").value("Duration must be greater than 0"));

        verify(internshipService, never()).create(any(CreateInternshipRequest.class));
    }
}

