package com.internflow.api.mentor;

import com.internflow.api.internship.Internship;
import com.internflow.api.internship.InternshipRepository;
import com.internflow.api.internship.InternshipResponse;
import com.internflow.api.internship.InternshipService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipService internshipService;

    public MentorService(MentorRepository mentorRepository, InternshipRepository internshipRepository, InternshipService internshipService) {
        this.mentorRepository = mentorRepository;
        this.internshipRepository = internshipRepository;
        this.internshipService = internshipService;
    }

    public List<MentorResponse> findAllMentors() {
        return mentorRepository.findAll().stream()
                .map(this::toMentorResponse)
                .toList();
    }

    public Optional<MentorResponse> findMentorById(Long id) {
        return this.mentorRepository.findById(id).map(this::toMentorResponse);
    }

    public MentorResponse createMentor(CreateMentorRequest request) {
        Mentor mentor = new Mentor(
                request.firstName(),
                request.lastName(),
                request.email()
        );
        Mentor savedMentor = mentorRepository.save(mentor);
        return toMentorResponse(savedMentor);
    }

    public Optional<InternshipResponse> assignMentorToInternship(Long internshipId, Long mentorId) {
        Internship internship = internshipRepository.findById(internshipId).orElse(null);
        if (internship == null) {return Optional.empty();}

        Mentor mentor = mentorRepository.findById(mentorId).orElse(null);
        if (mentor == null) {return Optional.empty();}

        internship.assignMentor(mentor);
        Internship savedInternship = internshipRepository.save(internship);
        return Optional.of(this.internshipService.toInternshipResponse(savedInternship));
    }

    public MentorResponse toMentorResponse(Mentor mentor) {
        return new MentorResponse(
                mentor.getId(),
                mentor.getFirstName(),
                mentor.getLastName(),
                mentor.getEmail()
        );
    }

}
