package com.internflow.api.internship;

public record InternshipResponse(
        Long id,
        String title,
        String company,
        Integer durationInMonths,
        InternshipStatus status,
        Long studentId
) {
}
