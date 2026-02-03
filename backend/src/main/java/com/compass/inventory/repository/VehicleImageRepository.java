package com.compass.inventory.repository;

import com.compass.inventory.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, UUID> {
    List<VehicleImage> findByVehicleId(UUID vehicleId);
    List<VehicleImage> findByVehicleIdAndIsPrimaryTrue(UUID vehicleId);
    java.util.Optional<VehicleImage> findFirstByVehicleIdAndIsPrimaryTrue(UUID vehicleId);
}
