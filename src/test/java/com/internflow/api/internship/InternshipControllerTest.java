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
        InternshipResponse response = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);

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
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        InternshipResponse internship2 = internshipResponse(2L, "Backend Internship", "Siemens", 8, InternshipStatus.OPEN);

        when(internshipService.findAllInternships(null, null)).thenReturn(List.of(internship1, internship2));

        mockMvc.perform(get("/internships")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Java Internship"))
                .andExpect(jsonPath("$[1].title").value("Backend Internship"));
    }

    @Test
    void getAllInternshipsShouldFilterByStatus() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        when(internshipService.findAllInternships(InternshipStatus.OPEN, null)).thenReturn(List.of(internship1));

        mockMvc.perform(get("/internships?status=OPEN")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(InternshipStatus.OPEN, null);
    }

    @Test
    void getAllInternshipsShouldFilterByCompany() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        when(internshipService.findAllInternships(null, "BMW")).thenReturn(List.of(internship1));

        mockMvc.perform(get("/internships?company=BMW")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].company").value("BMW"))
                .andExpect(jsonPath("$[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(null, "BMW");
    }

    @Test
    void getAllInternshipsShouldFilterByStatusAndCompany() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        when(internshipService.findAllInternships(InternshipStatus.OPEN, "BMW")).thenReturn(List.of(internship1));

        mockMvc.perform(get("/internships?status=OPEN&company=BMW")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].company").value("BMW"))
                .andExpect(jsonPath("$[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(InternshipStatus.OPEN, "BMW");
    }

    @Test
    void createInternshipShouldReturnCreatedWhenRequestIsValid() throws Exception {
        InternshipResponse response = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        String requestBody = internshipRequestJson("Java Internship", "BMW", 6);

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

    @Test
    void updateInternshipStatusShouldReturnOkWhenInternshipExists() throws Exception {
        InternshipResponse response = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.COMPLETED);
        String requestBody = statusRequestJson(InternshipStatus.COMPLETED);

        when(internshipService.updateStatus(1L, InternshipStatus.COMPLETED))
                .thenReturn(Optional.of(response));

        mockMvc.perform(patch("/internships/1/status").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InternshipStatus.COMPLETED.name()));

    }

    @Test
    void updateInternshipStatusShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        String requestBody = statusRequestJson(InternshipStatus.COMPLETED);

        when(internshipService.updateStatus(1L, InternshipStatus.COMPLETED)).thenReturn(Optional.empty());
        mockMvc.perform(patch("/internships/1/status").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateInternshipStatusShouldReturnBadRequestWhenStatusIsInvalid() throws Exception {
        String requestBody = """
                   {
                       "status": "UNKNOWN"
                   }
                """;

        mockMvc.perform(patch("/internships/1/status").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body"))
                .andExpect(jsonPath("$.errors.request").value("Request body is malformed or contains invalid values"));
        verify(internshipService, never()).updateStatus(anyLong(), any(InternshipStatus.class));
    }

    @Test
    void updateInternshipShouldReturnOkWhenInternshipExists() throws Exception {
        InternshipResponse response = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        String requestBody = internshipRequestJson("Java Internship", "BMW", 6);
        when(internshipService.update(eq(1L), any(CreateInternshipRequest.class))).thenReturn(Optional.of(response));
        mockMvc.perform(put("/internships/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));
    }

    @Test
    void updateInternshipShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        String requestBody = internshipRequestJson("Java Internship", "BMW", 6);
        when(internshipService.update(eq(1L), any(CreateInternshipRequest.class))).thenReturn(Optional.empty());
        mockMvc.perform(put("/internships/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateInternshipShouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "title": "",
                  "company": "",
                  "durationInMonths": 0
                }
                """;

        mockMvc.perform(put("/internships/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.company").value("Company is required"))
                .andExpect(jsonPath("$.errors.durationInMonths").value("Duration must be greater than 0"));

        verify(internshipService, never()).update(anyLong(), any(CreateInternshipRequest.class));
    }

    @Test
    void deleteInternshipShouldReturnNoContentWhenInternshipExists() throws Exception {
        when(internshipService.delete(1L)).thenReturn(true);
        mockMvc.perform(delete("/internships/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteInternshipShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        when(internshipService.delete(1L)).thenReturn(false);
        mockMvc.perform(delete("/internships/1"))
                .andExpect(status().isNotFound());
    }

    private InternshipResponse internshipResponse(
            Long id,
            String title,
            String company,
            Integer durationInMonths,
            InternshipStatus status
    ) {
        return new InternshipResponse(id, title, company, durationInMonths, status);
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

    private String statusRequestJson(InternshipStatus status) {
        return """
                 {
                     "status": "%s"
                 }
                """.formatted(status);
    }
}

