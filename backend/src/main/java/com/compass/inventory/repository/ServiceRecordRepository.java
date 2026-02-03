package com.compass.inventory.repository;

import com.compass.inventory.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, UUID> {
    List<ServiceRecord> findByVehicleId(UUID vehicleId);
}
