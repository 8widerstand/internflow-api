package com.internflow.api.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
