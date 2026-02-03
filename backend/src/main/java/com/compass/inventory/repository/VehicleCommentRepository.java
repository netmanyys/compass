package com.compass.inventory.repository;

import com.compass.inventory.entity.VehicleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleCommentRepository extends JpaRepository<VehicleComment, UUID> {
    List<VehicleComment> findByVehicleId(UUID vehicleId);
}
