package com.internflow.api.internship;

import com.internflow.api.common.error.ResourceNotFoundException;
import com.internflow.api.mentor.MentorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternshipController.class)
public class InternshipControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InternshipService internshipService;

    @MockitoBean
    private InternshipRepository internshipRepository;

    @Test
    void getInternshipByIdShouldReturnOkWhenInternshipExists() throws Exception {
        InternshipResponse response = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);

        when(internshipService.findInternshipById(1L)).thenReturn(response);

        mockMvc.perform(get("/internships/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Internship"))
                .andExpect(jsonPath("$.company").value("BMW"))
                .andExpect(jsonPath("$.durationInMonths").value(6))
                .andExpect(jsonPath("$.status").value(InternshipStatus.OPEN.name()));
    }

    @Test
    void getInternshipByIdShouldReturnNotFoundWhenInternshipDoesNotExist() throws Exception {
        when(internshipService.findInternshipById(1L))
                .thenThrow(new ResourceNotFoundException("Internship not found with id: 1"));
        mockMvc.perform(get("/internships/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.errors.resource").value("Internship not found with id: 1"));
    }

    @Test
    void getAllInternshipsShouldReturnOkWithInternships() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        InternshipResponse internship2 = internshipResponse(2L, "Backend Internship", "Siemens", 8, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1, internship2), pageable, 2);

        when(internshipService.findAllInternships(null, null, pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Java Internship"))
                .andExpect(jsonPath("$.content[1].title").value("Backend Internship"));
    }

    @Test
    void getAllInternshipsShouldFilterByStatus() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);

        when(internshipService.findAllInternships(InternshipStatus.OPEN, null, pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?status=OPEN")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(InternshipStatus.OPEN, null, pageable);
    }

    @Test
    void getAllInternshipsShouldFilterByCompany() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(null, "bm", pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?company=bm")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].company").value("BMW"))
                .andExpect(jsonPath("$.content[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(null, "bm", pageable);
    }

    @Test
    void getAllInternshipsShouldFilterByStatusAndCompany() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(InternshipStatus.OPEN, "bm", pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?status=OPEN&company=bm")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].company").value("BMW"))
                .andExpect(jsonPath("$.content[0].status").value(InternshipStatus.OPEN.name()));

        verify(internshipService).findAllInternships(InternshipStatus.OPEN, "bm", pageable);
    }

    @Test
    void getAllInternshipsShouldTreatBlankCompanyAsNoCompanyFilter() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(null, null, pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?company=  ")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(internshipService).findAllInternships(null, null, pageable);
    }

    @Test
    void getAllInternshipsShouldTrimCompanyFilter() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(null, "BMW", pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?company= BMW ")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(internshipService).findAllInternships(null, "BMW", pageable);
    }

    @Test
    void getAllInternshipsShouldReturnBadRequestWhenStatusParameterIsInvalid() throws Exception {

        mockMvc.perform(get("/internships?status=UNKNOWN")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.status").value("Invalid internship status"));

        verify(internshipService, never()).findAllInternships(any(), any(), any(Pageable.class));
    }

    @Test
    void getAllInternshipsShouldUsePageAndSizeParameters() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "id"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(null, null, pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?page=1&size=5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1));

        verify(internshipService).findAllInternships(null, null, pageable);
    }

    @Test
    void getAllInternshipsShouldReturnBadRequestWhenPageIsNegative() throws Exception {
        mockMvc.perform(get("/internships?page=-1&size=10")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.page").value("Page must be greater than or equal to 0"));
        verify(internshipService, never()).findAllInternships(any(), any(), any(Pageable.class));
    }

    @Test
    void getAllInternshipsShouldReturnBadRequestWhenSizeIsZero() throws Exception {
        mockMvc.perform(get("/internships?page=0&size=0")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.size").value("Size must be greater than 0"));

        verify(internshipService, never()).findAllInternships(any(), any(), any(Pageable.class));
    }

    @Test
    void getAllInternshipsShouldUseSortParameter() throws Exception {
        InternshipResponse internship1 = internshipResponse(1L, "Java Internship", "BMW", 6, InternshipStatus.OPEN);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "company"));
        Page<InternshipResponse> pageResult = new PageImpl<>(List.of(internship1), pageable, 1);
        when(internshipService.findAllInternships(null, null, pageable)).thenReturn(pageResult);

        mockMvc.perform(get("/internships?sort=company,desc")).andExpect(status().isOk());
        verify(internshipService).findAllInternships(null, null, pageable);
    }

    @Test
    void getAllInternshipsShouldReturnBadRequestWhenSortFieldIsInvalid() throws Exception {
        mockMvc.perform(get("/internships?sort=randomField,asc")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.sort").value("Invalid sort field"));
        verify(internshipService, never()).findAllInternships(any(), any(), any(Pageable.class));
    }

    @Test
    void getAllInternshipsShouldReturnBadRequestWhenSortDirectionIsInvalid() throws Exception {
        mockMvc.perform(get("/internships?sort=company,wrong")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameter"))
                .andExpect(jsonPath("$.errors.sort").value("Invalid sort direction"));

        verify(internshipService, never()).findAllInternships(any(), any(), any(Pageable.class));
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

        when(internshipService.updateStatus(1L, InternshipStatus.COMPLETED)).thenReturn(empty());
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
        when(internshipService.update(eq(1L), any(CreateInternshipRequest.class))).thenReturn(empty());
        mockMvc.perform(put("/internships/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(content().string(""))
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
        mockMvc.perform(delete("/internships/1"))
                .andExpect(status().isNoContent());

        verify(internshipService).delete(1L);
    }

    private InternshipResponse internshipResponse(
            Long id,
            String title,
            String company,
            Integer durationInMonths,
            InternshipStatus status
    ) {
        return new InternshipResponse(id, title, company, durationInMonths, status, null, null);
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

