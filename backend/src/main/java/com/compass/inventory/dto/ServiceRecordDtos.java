package com.compass.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ServiceRecordDtos {
    public record ServiceRecordRequest(
        @NotNull UUID vehicleId,
        @NotNull LocalDate serviceDate,
        @NotBlank String vendor,
        Integer odometer,
        @NotBlank String category,
        @NotBlank String description,
        @NotNull @DecimalMin("0.0") BigDecimal cost,
        String invoiceUrl
    ) {}

    public record ServiceRecordResponse(
        UUID id,
        UUID vehicleId,
        LocalDate serviceDate,
        String vendor,
        Integer odometer,
        String category,
        String description,
        BigDecimal cost,
        String invoiceUrl,
        OffsetDateTime createdAt,
        String createdBy
    ) {}
}
