package com.internflow.api.task;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(
        @NotBlank(message = "Title is required") String title,
        String description
) {
}
