package com.compass.inventory.service;

import com.compass.inventory.dto.ServiceRecordDtos;
import com.compass.inventory.entity.ServiceRecord;
import com.compass.inventory.entity.Vehicle;
import com.compass.inventory.repository.ServiceRecordRepository;
import com.compass.inventory.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceRecordService {
    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleRepository vehicleRepository;

    public ServiceRecordService(ServiceRecordRepository serviceRecordRepository, VehicleRepository vehicleRepository) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<ServiceRecordDtos.ServiceRecordResponse> list(UUID vehicleId) {
        List<ServiceRecord> records = vehicleId == null ? serviceRecordRepository.findAll() : serviceRecordRepository.findByVehicleId(vehicleId);
        return records.stream().map(this::toResponse).toList();
    }

    public ServiceRecordDtos.ServiceRecordResponse get(UUID id) {
        return toResponse(serviceRecordRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Service record not found")));
    }

    public ServiceRecordDtos.ServiceRecordResponse create(ServiceRecordDtos.ServiceRecordRequest request, String username) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        ServiceRecord record = new ServiceRecord();
        record.setId(UUID.randomUUID());
        record.setVehicle(vehicle);
        record.setServiceDate(request.serviceDate());
        record.setVendor(request.vendor());
        record.setOdometer(request.odometer());
        record.setCategory(request.category());
        record.setDescription(request.description());
        record.setCost(request.cost());
        record.setInvoiceUrl(request.invoiceUrl());
        record.setCreatedAt(OffsetDateTime.now());
        record.setCreatedBy(username);
        return toResponse(serviceRecordRepository.save(record));
    }

    public ServiceRecordDtos.ServiceRecordResponse update(UUID id, ServiceRecordDtos.ServiceRecordRequest request) {
        ServiceRecord record = serviceRecordRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Service record not found"));
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        record.setVehicle(vehicle);
        record.setServiceDate(request.serviceDate());
        record.setVendor(request.vendor());
        record.setOdometer(request.odometer());
        record.setCategory(request.category());
        record.setDescription(request.description());
        record.setCost(request.cost());
        record.setInvoiceUrl(request.invoiceUrl());
        return toResponse(serviceRecordRepository.save(record));
    }

    public void delete(UUID id) {
        serviceRecordRepository.deleteById(id);
    }

    private ServiceRecordDtos.ServiceRecordResponse toResponse(ServiceRecord record) {
        return new ServiceRecordDtos.ServiceRecordResponse(
            record.getId(),
            record.getVehicle().getId(),
            record.getServiceDate(),
            record.getVendor(),
            record.getOdometer(),
            record.getCategory(),
            record.getDescription(),
            record.getCost(),
            record.getInvoiceUrl(),
            record.getCreatedAt(),
            record.getCreatedBy()
        );
    }
}
