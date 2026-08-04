package com.internflow.api.internship;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InternshipService {

    public List<InternshipResponse> findAllInternships() {
        return List.of(new InternshipResponse(1L, "Java Backend Internship", "Docufy", 6),
                new InternshipResponse(2L, "Spring Boot Internship", "TechCorp", 3));
    }

    public InternshipResponse findInternshipById(Long id) {

        if (id.equals(1L)) {
            return new InternshipResponse(1L, "Java Backend Internship", "Docufy", 6);
        } else if (id.equals(2L)) {
            return new InternshipResponse(2L, "Spring Boot Internship", "TechCorp", 3);
        } else return null;
    }
}
