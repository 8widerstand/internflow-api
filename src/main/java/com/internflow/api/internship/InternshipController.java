package com.internflow.api.internship;

import jakarta.validation.Valid;
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
    public List<InternshipResponse> internships(
            @RequestParam(required = false) InternshipStatus status,
            @RequestParam(required = false) String company
    ) {
        String companyValue = normalizeCompany(company);
        return internshipService.findAllInternships(status, companyValue);
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
    public ResponseEntity<InternshipResponse> updateInternship(@PathVariable Long id, @Valid @RequestBody CreateInternshipRequest createInternshipRequest) {
        return internshipService.update(id, createInternshipRequest)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("internships/{id}/status")
    public ResponseEntity<InternshipResponse> updateInternshipStatus(@PathVariable Long id, @Valid @RequestBody UpdateInternshipStatusRequest updateStatusRequest) {
        return internshipService.updateStatus(id, updateStatusRequest.status())
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
}
