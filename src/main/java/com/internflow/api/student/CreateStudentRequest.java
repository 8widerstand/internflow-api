package com.internflow.api.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        String university,
        @PastOrPresent(message = "Birth date cannot be in the future") LocalDate birthDate
) {
}
