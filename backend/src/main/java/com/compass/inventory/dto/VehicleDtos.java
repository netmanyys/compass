package com.compass.inventory.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VehicleDtos {
    public record VehicleRequest(
        @NotBlank String vin,
        @NotBlank String manufacture,
        @NotBlank String model,
        @Min(1980) @Max(2100) int year,
        @NotBlank String color,
        String trim,
        @NotBlank String bodyType,
        String drivetrain,
        String engine,
        String transmission,
        String fuelType,
        @Min(0) int mileage,
        @NotBlank String conditionGrade,
        @NotBlank String titleStatus,
        String carfaxUrl,
        @NotBlank String status,
        @NotBlank String location,
        @NotNull @DecimalMin("0.0") BigDecimal purchasePrice,
        @NotNull @DecimalMin("0.0") BigDecimal listPrice,
        @DecimalMin("0.0") BigDecimal marketPrice,
        String notes,
        String acquisitionChannel,
        LocalDate purchaseDate,
        LocalDate expectedReadyDate,
        String documents,
        String reservation,
        Set<UUID> packageIds
    ) {}

    public record VehicleResponse(
        UUID id,
        String vin,
        String manufacture,
        String model,
        int year,
        String color,
        String trim,
        String bodyType,
        String drivetrain,
        String engine,
        String transmission,
        String fuelType,
        int mileage,
        String conditionGrade,
        String titleStatus,
        String carfaxUrl,
        String status,
        String location,
        BigDecimal purchasePrice,
        BigDecimal listPrice,
        BigDecimal marketPrice,
        String notes,
        String acquisitionChannel,
        LocalDate purchaseDate,
        LocalDate expectedReadyDate,
        String documents,
        String reservation,
        String primaryImageUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String createdBy,
        String updatedBy,
        List<PackageDto> packages
    ) {}
}
