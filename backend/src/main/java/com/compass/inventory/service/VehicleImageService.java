package com.compass.inventory.service;

import com.compass.inventory.dto.VehicleImageDtos;
import com.compass.inventory.entity.Vehicle;
import com.compass.inventory.entity.VehicleImage;
import com.compass.inventory.repository.VehicleImageRepository;
import com.compass.inventory.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleImageService {
    private final VehicleImageRepository imageRepository;
    private final VehicleRepository vehicleRepository;
    private final Path uploadDir;

    public VehicleImageService(
        VehicleImageRepository imageRepository,
        VehicleRepository vehicleRepository,
        @Value("${app.upload.dir}") String uploadDir
    ) {
        this.imageRepository = imageRepository;
        this.vehicleRepository = vehicleRepository;
        this.uploadDir = Paths.get(uploadDir);
    }

    public List<VehicleImageDtos.ImageResponse> list(UUID vehicleId) {
        List<VehicleImage> images = vehicleId == null ? imageRepository.findAll() : imageRepository.findByVehicleId(vehicleId);
        return images.stream().map(this::toResponse).toList();
    }

    public VehicleImageDtos.ImageResponse upload(UUID vehicleId, MultipartFile image, String caption, Boolean isPrimary, Integer sortOrder, String username) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }
        String ext = getExtension(image.getOriginalFilename());
        YearMonth ym = YearMonth.now();
        String relativeDir = "vehicles/" + ym.getYear() + "/" + String.format("%02d", ym.getMonthValue());
        String filename = UUID.randomUUID() + (ext.isBlank() ? "" : "." + ext);
        Path targetDir = uploadDir.resolve(relativeDir);
        Path targetFile = targetDir.resolve(filename);
        try {
            Files.createDirectories(targetDir);
            image.transferTo(targetFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to store file");
        }

        if (Boolean.TRUE.equals(isPrimary)) {
            clearPrimary(vehicleId);
        }

        VehicleImage entity = new VehicleImage();
        entity.setId(UUID.randomUUID());
        entity.setVehicle(vehicle);
        entity.setFilePath(relativeDir + "/" + filename);
        entity.setOriginalFilename(image.getOriginalFilename());
        entity.setContentType(image.getContentType());
        entity.setFileSize(image.getSize());
        entity.setCaption(caption);
        entity.setPrimary(Boolean.TRUE.equals(isPrimary));
        entity.setSortOrder(sortOrder == null ? 0 : sortOrder);
        entity.setUploadedAt(OffsetDateTime.now());
        entity.setUploadedBy(username);

        return toResponse(imageRepository.save(entity));
    }

    public VehicleImageDtos.ImageResponse update(UUID id, VehicleImageDtos.ImagePatchRequest request) {
        VehicleImage image = imageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found"));
        if (request.isPrimary() != null && request.isPrimary()) {
            clearPrimary(image.getVehicle().getId());
            image.setPrimary(true);
        } else if (request.isPrimary() != null) {
            image.setPrimary(false);
        }
        if (request.caption() != null) {
            image.setCaption(request.caption());
        }
        if (request.sortOrder() != null) {
            image.setSortOrder(request.sortOrder());
        }
        return toResponse(imageRepository.save(image));
    }

    public void delete(UUID id) {
        VehicleImage image = imageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Image not found"));
        Path path = uploadDir.resolve(image.getFilePath());
        imageRepository.delete(image);
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    private void clearPrimary(UUID vehicleId) {
        List<VehicleImage> primary = imageRepository.findByVehicleIdAndIsPrimaryTrue(vehicleId);
        for (VehicleImage img : primary) {
            img.setPrimary(false);
        }
        imageRepository.saveAll(primary);
    }

    private VehicleImageDtos.ImageResponse toResponse(VehicleImage image) {
        String imageUrl = "/uploads/" + image.getFilePath();
        return new VehicleImageDtos.ImageResponse(
            image.getId(),
            image.getVehicle().getId(),
            imageUrl,
            image.isPrimary(),
            image.getCaption(),
            image.getSortOrder(),
            image.getUploadedAt()
        );
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int idx = filename.lastIndexOf('.');
        return idx == -1 ? "" : filename.substring(idx + 1);
    }
}
