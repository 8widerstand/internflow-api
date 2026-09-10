package com.internflow.api.student;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateStudentRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        String university,
        LocalDate birthDate
) {
}
