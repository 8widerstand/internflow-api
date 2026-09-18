package com.internflow.api.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String username, @NotBlank String password)
{}
