package com.internflow.api.mentor;

public record MentorResponse(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
