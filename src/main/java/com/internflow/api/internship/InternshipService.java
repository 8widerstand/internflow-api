package com.internflow.api.internship;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InternshipService {

    private final List<InternshipResponse> internships = new ArrayList<>(
            List.of(new InternshipResponse(1L, "Java Backend Internship", "Docufy", 6),
                    new InternshipResponse(2L, "Spring Boot Internship", "TechCorp", 3)));


    public List<InternshipResponse> findAllInternships() {
        return this.internships;
    }

    public InternshipResponse findInternshipById(Long id) {

        return this.internships.stream()
                .filter(internship -> internship.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
