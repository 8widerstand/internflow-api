package com.internflow.api.internship;

import jakarta.validation.constraints.NotNull;

public record UpdateInternshipStatusRequest(
        @NotNull(message = "Status is required") InternshipStatus status
) {
}
