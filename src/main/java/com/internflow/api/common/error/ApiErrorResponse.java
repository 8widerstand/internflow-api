package com.internflow.api.common.error;

import java.util.Map;

public record ApiErrorResponse(
        String message,
        Map<String, String> errors
) {
}
