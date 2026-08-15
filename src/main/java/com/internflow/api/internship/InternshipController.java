package com.internflow.api.internship;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping("/internships")
    public Page<InternshipResponse> internships(
            @RequestParam(required = false) InternshipStatus status,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        validatePagination(page, size);
        Sort sortValue = parseSort(sort);
        String companyValue = normalizeCompany(company);
        Pageable pageable = PageRequest.of(page, size, sortValue);
        return internshipService.findAllInternships(status, companyValue, pageable);
    }

    @GetMapping("/internships/{id}")
    public ResponseEntity<InternshipResponse> getInternshipById(@PathVariable Long id) {
        return internshipService.findInternshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/internships")
    public ResponseEntity<InternshipResponse> createInternship(
            @Valid @RequestBody CreateInternshipRequest createInternshipRequest
    ) {
        InternshipResponse internship = internshipService.create(createInternshipRequest);
        URI location = URI.create("/internships/" + internship.id());

        return ResponseEntity.created(location).body(internship);
    }

    @PutMapping("/internships/{id}")
    public ResponseEntity<InternshipResponse> updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody CreateInternshipRequest createInternshipRequest
    ) {
        return internshipService.update(id, createInternshipRequest)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/internships/{id}/status")
    public ResponseEntity<InternshipResponse> updateInternshipStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInternshipStatusRequest updateStatusRequest
    ) {
        return internshipService.updateStatus(id, updateStatusRequest.status())
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //Assign a student to a specific internship
    @PatchMapping("/internships/{internshipId}/student/{studentId}")
    public ResponseEntity<InternshipResponse> assignStudentToInternship(
            @PathVariable Long internshipId,
            @PathVariable Long studentId
    ) {
        return internshipService.assignInternship(internshipId, studentId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        if (internshipService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private String normalizeCompany(String company) {
        if (company == null || company.trim().isEmpty()) {
            return null;
        }
        return company.trim();
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page: Page must be greater than or equal to 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size: Size must be greater than 0");
        }
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        if (!List.of("id", "title", "company", "durationInMonths", "status").contains(field)) {
            throw new IllegalArgumentException("sort: Invalid sort field");
        }

        String direction = "asc";
        if (parts.length > 1) {
            direction = parts[1];
        }
        String normalizedDirection = direction.toLowerCase();

        if (!List.of("asc", "desc").contains(normalizedDirection)) {
            throw new IllegalArgumentException("sort: Invalid sort direction");
        }

        Sort.Direction directionValue = Sort.Direction.fromString(normalizedDirection);
        return Sort.by(directionValue, field);
    }
}
