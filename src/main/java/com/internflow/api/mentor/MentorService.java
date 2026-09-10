package com.internflow.api.mentor;

import com.internflow.api.common.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;

    public MentorService(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }

    public List<MentorResponse> findAllMentors() {
        return mentorRepository.findAll().stream()
                .map(this::toMentorResponse)
                .toList();
    }

    public MentorResponse findMentorById(Long id) {
        return mentorRepository.findById(id)
                .map(this::toMentorResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + id));
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

    public MentorResponse toMentorResponse(Mentor mentor) {
        return new MentorResponse(
                mentor.getId(),
                mentor.getFirstName(),
                mentor.getLastName(),
                mentor.getEmail()
        );
    }
}
