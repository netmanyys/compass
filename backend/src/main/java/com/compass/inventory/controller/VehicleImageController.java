package com.compass.inventory.controller;

import com.compass.inventory.dto.VehicleImageDtos;
import com.compass.inventory.service.VehicleImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle-images")
public class VehicleImageController {
    private final VehicleImageService imageService;

    public VehicleImageController(VehicleImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public List<VehicleImageDtos.ImageResponse> list(@RequestParam(required = false) UUID vehicleId) {
        return imageService.list(vehicleId);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public VehicleImageDtos.ImageResponse upload(
        @RequestParam UUID vehicleId,
        @RequestParam MultipartFile image,
        @RequestParam(required = false) String caption,
        @RequestParam(required = false) Boolean isPrimary,
        @RequestParam(required = false) Integer sortOrder
    ) {
        return imageService.upload(vehicleId, image, caption, isPrimary, sortOrder, currentUsername());
    }

    @PatchMapping("/{id}")
    public VehicleImageDtos.ImageResponse update(@PathVariable UUID id, @RequestBody VehicleImageDtos.ImagePatchRequest request) {
        return imageService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        imageService.delete(id);
    }

    private String currentUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
