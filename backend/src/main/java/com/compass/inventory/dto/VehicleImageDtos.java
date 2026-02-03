package com.compass.inventory.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class VehicleImageDtos {
    public record ImageResponse(
        UUID id,
        UUID vehicleId,
        String imageUrl,
        boolean isPrimary,
        String caption,
        int sortOrder,
        OffsetDateTime uploadedAt
    ) {}

    public record ImagePatchRequest(Boolean isPrimary, String caption, Integer sortOrder) {}
}
