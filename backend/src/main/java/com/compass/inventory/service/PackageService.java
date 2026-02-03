package com.compass.inventory.service;

import com.compass.inventory.dto.PackageDto;
import com.compass.inventory.entity.PackageEntity;
import com.compass.inventory.repository.PackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PackageService {
    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<PackageDto> list() {
        return packageRepository.findAll().stream()
            .map(pkg -> new PackageDto(pkg.getId(), pkg.getName(), pkg.getDescription()))
            .toList();
    }

    public PackageDto create(PackageDto request) {
        PackageEntity entity = new PackageEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity = packageRepository.save(entity);
        return new PackageDto(entity.getId(), entity.getName(), entity.getDescription());
    }

    public PackageDto update(UUID id, PackageDto request) {
        PackageEntity entity = packageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Package not found"));
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity = packageRepository.save(entity);
        return new PackageDto(entity.getId(), entity.getName(), entity.getDescription());
    }

    public void delete(UUID id) {
        packageRepository.deleteById(id);
    }
}
