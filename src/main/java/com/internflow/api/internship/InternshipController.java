package com.internflow.api.internship;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipController(InternshipService internshipService) {
        this.internshipService = internshipService;
    }

    @GetMapping("/internships")
    public List<InternshipResponse> internships() {
        return internshipService.findAllInternships();
    }

    @GetMapping("/internships/{id}")
    public ResponseEntity<InternshipResponse> getInternshipById(@PathVariable Long id) {
        return internshipService.findInternshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/internships")
    public ResponseEntity<InternshipResponse> createInternship(
            @Valid @RequestBody CreateInternshipRequest createInternshipRequest)
    {
        InternshipResponse internship = internshipService.create(createInternshipRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(internship);
    }

    @PutMapping("/internships/{id}")
    public ResponseEntity<InternshipResponse> updateInternship(@PathVariable Long id, @Valid @RequestBody CreateInternshipRequest createInternshipRequest){
        InternshipResponse updatedInternship = internshipService.update(id, createInternshipRequest);
        if (updatedInternship == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedInternship);
    }

    @DeleteMapping("/internships/{id}")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id) {
        if (internshipService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
