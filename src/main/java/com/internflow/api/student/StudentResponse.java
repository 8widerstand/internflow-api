package com.internflow.api.student;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String university,
        LocalDate birthDate
) {
}
