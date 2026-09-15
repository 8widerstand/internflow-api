package com.internflow.api.mentor;

import com.internflow.api.common.error.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Tag(
        name = "Mentors",
        description = "Create and retrieve mentors"
)
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

    @Operation(summary = "Create a mentor")
    @ApiResponse(responseCode = "201", description = "Mentor created")
    @ApiResponse(responseCode = "400", description = "Invalid mentor data",
     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    @PostMapping("/mentors")
    public ResponseEntity<MentorResponse> createMentor(
            @Valid @RequestBody CreateMentorRequest request
    ){
        MentorResponse createdMentor = this.mentorService.createMentor(request);
        URI location = URI.create("/mentors/" + createdMentor.id());
        return ResponseEntity.created(location).body(createdMentor);
    }
}
