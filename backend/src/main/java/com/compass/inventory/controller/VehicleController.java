package com.compass.inventory.controller;

import com.compass.inventory.dto.VehicleDtos;
import com.compass.inventory.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public Page<VehicleDtos.VehicleResponse> search(
        @RequestParam(name = "vin", required = false) String vin,
        @RequestParam(name = "manufacture", required = false) String manufacture,
        @RequestParam(name = "model", required = false) String model,
        @RequestParam(name = "yearMin", required = false) Integer yearMin,
        @RequestParam(name = "yearMax", required = false) Integer yearMax,
        @RequestParam(name = "priceMin", required = false) BigDecimal priceMin,
        @RequestParam(name = "priceMax", required = false) BigDecimal priceMax,
        @RequestParam(name = "mileageMin", required = false) Integer mileageMin,
        @RequestParam(name = "mileageMax", required = false) Integer mileageMax,
        @RequestParam(name = "color", required = false) String color,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "conditionGrade", required = false) String conditionGrade,
        @RequestParam(name = "packageIds", required = false) String packageIds,
        @RequestParam(name = "keyword", required = false) String keyword,
        Pageable pageable
    ) {
        List<UUID> packages = packageIds == null || packageIds.isBlank() ? List.of() :
            List.of(packageIds.split(",")).stream().map(UUID::fromString).toList();

        VehicleService.VehicleSearchCriteria criteria = new VehicleService.VehicleSearchCriteria(
            vin, manufacture, model, yearMin, yearMax, priceMin, priceMax, mileageMin, mileageMax,
            color, status, conditionGrade, packages, keyword
        );
        return vehicleService.search(criteria, pageable);
    }

    @GetMapping("/{id}")
    public VehicleDtos.VehicleResponse get(@PathVariable UUID id) {
        return vehicleService.get(id);
    }

    @PostMapping
    public VehicleDtos.VehicleResponse create(@Valid @RequestBody VehicleDtos.VehicleRequest request) {
        return vehicleService.create(request, currentUsername());
    }

    @PatchMapping("/{id}")
    public VehicleDtos.VehicleResponse update(@PathVariable UUID id, @Valid @RequestBody VehicleDtos.VehicleRequest request) {
        return vehicleService.update(id, request, currentUsername());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        vehicleService.delete(id);
    }

    private String currentUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
