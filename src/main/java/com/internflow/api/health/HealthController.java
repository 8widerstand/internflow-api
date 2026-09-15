package com.internflow.api.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(
        name = "Health",
        description = "Check API availability"
)
@RestController
public class HealthController {
    @Operation(summary = "Check API health")
    @GetMapping("/health")
    public String health() {
        return "Interflow API is up and running ";
    }

}
