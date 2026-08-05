package com.internflow.api.internship;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InternshipService {

    private final List<InternshipResponse> internships = new ArrayList<>(
            List.of(new InternshipResponse(1L, "Java Backend Internship", "Docufy", 6),
                    new InternshipResponse(2L, "Spring Boot Internship", "TechCorp", 3)));


    public List<InternshipResponse> findAllInternships() {
        return this.internships;
    }

    public Optional<InternshipResponse> findInternshipById(Long id) {
        return this.internships.stream()
                .filter(internship -> internship.id().equals(id))
                .findFirst();
    }

    public InternshipResponse create(CreateInternshipRequest createInternshipRequest) {
        Long newId = this.internships.size() + 1L;
        InternshipResponse newInternship = new InternshipResponse(newId, createInternshipRequest.title(), createInternshipRequest.company(),
                createInternshipRequest.durationInMonths());
        this.internships.add(newInternship);
        return newInternship;
    }

    public InternshipResponse update(Long id, CreateInternshipRequest request) {
        for (int i = 0; i < this.internships.size(); i++) {
            InternshipResponse existing = internships.get(i);

            if (existing.id().equals(id)) {
                InternshipResponse newInternship = new InternshipResponse(existing.id(), request.title(), request.company(), request.durationInMonths());
                this.internships.set(i, newInternship);
                return newInternship;
            }
        }
        return null;
    }

    public boolean delete(Long id) {
        return this.internships.removeIf(internship -> internship.id().equals(id));
    }
}
