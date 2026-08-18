package com.internflow.api.mentor;

import com.internflow.api.internship.InternshipResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class MentorController {
    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping("/mentors")
    public List<MentorResponse> getAllMentors() {
        return  mentorService.findAllMentors();
    }

    @GetMapping("/mentors/{id}")
    public ResponseEntity<MentorResponse> getMentorById(@PathVariable Long id) {
        return this.mentorService.findMentorById(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/mentors")
    public ResponseEntity<MentorResponse> createMentor(
            @Valid @RequestBody CreateMentorRequest request
    ){
        MentorResponse createdMentor = this.mentorService.createMentor(request);
        URI location = URI.create("/mentors/" + createdMentor.id());
        return ResponseEntity.created(location).body(createdMentor);
    }

    @PatchMapping("/internships/{internshipId}/mentor/{mentorId}")
    public ResponseEntity<InternshipResponse> assignMentorToInternship(
            @PathVariable Long internshipId,
            @PathVariable Long mentorId)
    {
        return this.mentorService.assignMentorToInternship(internshipId, mentorId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
