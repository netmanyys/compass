package com.compass.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public class VehicleCommentDtos {
    public record CommentRequest(@NotNull UUID vehicleId, @NotBlank String comment) {}

    public record CommentResponse(UUID id, UUID vehicleId, String comment, OffsetDateTime createdAt, String createdBy) {}
}
