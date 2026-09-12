package com.internflow.api.task;

import jakarta.validation.constraints.NotNull;

public record UpdateTaskCompletedRequest(
        @NotNull(message = "Completed is required")
        Boolean completed
) {
}
