package com.internflow.api.mentor;

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
    public MentorResponse getMentorById(@PathVariable Long id) {
        return this.mentorService.findMentorById(id);
    }

    @PostMapping("/mentors")
    public ResponseEntity<MentorResponse> createMentor(
            @Valid @RequestBody CreateMentorRequest request
    ){
        MentorResponse createdMentor = this.mentorService.createMentor(request);
        URI location = URI.create("/mentors/" + createdMentor.id());
        return ResponseEntity.created(location).body(createdMentor);
    }

}
