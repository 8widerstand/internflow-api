package com.internflow.api.internship;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<InternshipResponse> internshipById(@PathVariable Long id) {
        InternshipResponse internship = internshipService.findInternshipById(id);
        if (internship == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(internship);
    }
}
