package com.internflow.api.task;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Boolean completed,
        Long internshipId
) {
}
