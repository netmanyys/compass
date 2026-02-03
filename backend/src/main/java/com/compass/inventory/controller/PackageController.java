package com.compass.inventory.controller;

import com.compass.inventory.dto.PackageDto;
import com.compass.inventory.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/packages")
public class PackageController {
    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    public List<PackageDto> list() {
        return packageService.list();
    }

    @PostMapping
    public PackageDto create(@Valid @RequestBody PackageDto request) {
        return packageService.create(request);
    }

    @PatchMapping("/{id}")
    public PackageDto update(@PathVariable UUID id, @Valid @RequestBody PackageDto request) {
        return packageService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        packageService.delete(id);
    }
}
