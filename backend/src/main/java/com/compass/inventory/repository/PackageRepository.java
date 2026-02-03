package com.compass.inventory.repository;

import com.compass.inventory.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PackageRepository extends JpaRepository<PackageEntity, UUID> {
}
