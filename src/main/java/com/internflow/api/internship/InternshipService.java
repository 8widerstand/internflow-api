package com.internflow.api.internship;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;

    public InternshipService(InternshipRepository internshipRepository) {
        this.internshipRepository = internshipRepository;
    }

    public List<InternshipResponse> findAllInternships(InternshipStatus status) {
        List<Internship> internships;

        if (status == null) {
            internships = internshipRepository.findAll();
        } else {
            internships = internshipRepository.findByStatus(status);
        }

        return internships.stream()
                .map(this::toInternshipResponse)
                .toList();
    }

    public Optional<InternshipResponse> findInternshipById(Long id) {
        return internshipRepository.findById(id)
                .map(this::toInternshipResponse);
    }

    public InternshipResponse create(CreateInternshipRequest request) {
        Internship internship = new Internship(
                request.title(),
                request.company(),
                request.durationInMonths()
        );

        Internship savedInternship = internshipRepository.save(internship);

        return toInternshipResponse(savedInternship);

    }

    public Optional<InternshipResponse> update(Long id, CreateInternshipRequest request) {
        Optional<Internship> internshipOptional = internshipRepository.findById(id);
        if (internshipOptional.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOptional.get();
        internship.update(request.title(), request.company(), request.durationInMonths());
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public Optional<InternshipResponse> updateStatus(Long id, InternshipStatus status) {
        Optional<Internship> internshipOptional = internshipRepository.findById(id);
        if (internshipOptional.isEmpty()) {
            return Optional.empty();
        }
        Internship internship = internshipOptional.get();
        internship.updateStatus(status);
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(toInternshipResponse(savedInternship));
    }

    public boolean delete(Long id) {
        if (!internshipRepository.existsById(id)) {
            return false;
        }

        internshipRepository.deleteById(id);
        return true;
    }

    InternshipResponse toInternshipResponse(Internship internship) {
        return new InternshipResponse(
                internship.getId(),
                internship.getTitle(),
                internship.getCompany(),
                internship.getDurationInMonths(),
                internship.getStatus()
        );
    }
}