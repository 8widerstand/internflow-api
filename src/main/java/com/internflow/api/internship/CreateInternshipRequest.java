package com.internflow.api.internship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateInternshipRequest(
        @NotBlank(message = "Title is required") String title,
        @NotBlank(message = "Company is required") String company,
        @NotNull(message = "Duration is required") @Positive(message = "Duration must be greater than 0")
        Integer durationInMonths
) {
}
