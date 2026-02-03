package com.compass.inventory.service;

import com.compass.inventory.dto.PackageDto;
import com.compass.inventory.dto.VehicleDtos;
import com.compass.inventory.entity.PackageEntity;
import com.compass.inventory.entity.Vehicle;
import com.compass.inventory.repository.PackageRepository;
import com.compass.inventory.repository.VehicleRepository;
import com.compass.inventory.spec.VehicleSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final PackageRepository packageRepository;
    private final com.compass.inventory.repository.VehicleImageRepository vehicleImageRepository;

    public VehicleService(VehicleRepository vehicleRepository, PackageRepository packageRepository, com.compass.inventory.repository.VehicleImageRepository vehicleImageRepository) {
        this.vehicleRepository = vehicleRepository;
        this.packageRepository = packageRepository;
        this.vehicleImageRepository = vehicleImageRepository;
    }

    public Page<VehicleDtos.VehicleResponse> search(VehicleSearchCriteria criteria, Pageable pageable) {
        Specification<Vehicle> spec = Specification.where(VehicleSpecifications.vinEquals(criteria.vin()))
            .and(VehicleSpecifications.manufactureLike(criteria.manufacture()))
            .and(VehicleSpecifications.modelLike(criteria.model()))
            .and(VehicleSpecifications.yearMin(criteria.yearMin()))
            .and(VehicleSpecifications.yearMax(criteria.yearMax()))
            .and(VehicleSpecifications.priceMin(criteria.priceMin()))
            .and(VehicleSpecifications.priceMax(criteria.priceMax()))
            .and(VehicleSpecifications.mileageMin(criteria.mileageMin()))
            .and(VehicleSpecifications.mileageMax(criteria.mileageMax()))
            .and(VehicleSpecifications.colorEquals(criteria.color()))
            .and(VehicleSpecifications.statusEquals(criteria.status()))
            .and(VehicleSpecifications.conditionEquals(criteria.conditionGrade()))
            .and(VehicleSpecifications.packageIdsIn(criteria.packageIds()))
            .and(VehicleSpecifications.keywordLike(criteria.keyword()));

        return vehicleRepository.findAll(spec, pageable).map(this::toResponse);
    }

    public VehicleDtos.VehicleResponse get(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return toResponse(vehicle);
    }

    public VehicleDtos.VehicleResponse create(VehicleDtos.VehicleRequest request, String username) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        applyRequest(vehicle, request, username, true);
        return toResponse(vehicleRepository.save(vehicle));
    }

    public VehicleDtos.VehicleResponse update(UUID id, VehicleDtos.VehicleRequest request, String username) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        applyRequest(vehicle, request, username, false);
        return toResponse(vehicleRepository.save(vehicle));
    }

    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }

    private void applyRequest(Vehicle vehicle, VehicleDtos.VehicleRequest request, String username, boolean isCreate) {
        vehicle.setVin(request.vin());
        vehicle.setManufacture(request.manufacture());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setColor(request.color());
        vehicle.setTrim(request.trim());
        vehicle.setBodyType(request.bodyType());
        vehicle.setDrivetrain(request.drivetrain());
        vehicle.setEngine(request.engine());
        vehicle.setTransmission(request.transmission());
        vehicle.setFuelType(request.fuelType());
        vehicle.setMileage(request.mileage());
        vehicle.setConditionGrade(request.conditionGrade());
        vehicle.setTitleStatus(request.titleStatus());
        vehicle.setCarfaxUrl(request.carfaxUrl());
        vehicle.setStatus(request.status());
        vehicle.setLocation(request.location());
        vehicle.setPurchasePrice(request.purchasePrice());
        vehicle.setListPrice(request.listPrice());
        vehicle.setMarketPrice(request.marketPrice());
        vehicle.setNotes(request.notes());
        vehicle.setAcquisitionChannel(request.acquisitionChannel());
        vehicle.setPurchaseDate(request.purchaseDate());
        vehicle.setExpectedReadyDate(request.expectedReadyDate());
        vehicle.setDocuments(request.documents());
        vehicle.setReservation(request.reservation());
        if (request.packageIds() != null) {
            Set<PackageEntity> packages = request.packageIds().stream()
                .map(id -> packageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Package not found: " + id)))
                .collect(Collectors.toSet());
            vehicle.setPackages(packages);
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (isCreate) {
            vehicle.setCreatedAt(now);
            vehicle.setCreatedBy(username);
        }
        vehicle.setUpdatedAt(now);
        vehicle.setUpdatedBy(username);
    }

    private VehicleDtos.VehicleResponse toResponse(Vehicle vehicle) {
        List<PackageDto> packages = vehicle.getPackages().stream()
            .map(pkg -> new PackageDto(pkg.getId(), pkg.getName(), pkg.getDescription()))
            .toList();
        String primaryImageUrl = vehicleImageRepository.findFirstByVehicleIdAndIsPrimaryTrue(vehicle.getId())
            .map(img -> "/uploads/" + img.getFilePath())
            .orElse(null);
        return new VehicleDtos.VehicleResponse(
            vehicle.getId(),
            vehicle.getVin(),
            vehicle.getManufacture(),
            vehicle.getModel(),
            vehicle.getYear(),
            vehicle.getColor(),
            vehicle.getTrim(),
            vehicle.getBodyType(),
            vehicle.getDrivetrain(),
            vehicle.getEngine(),
            vehicle.getTransmission(),
            vehicle.getFuelType(),
            vehicle.getMileage(),
            vehicle.getConditionGrade(),
            vehicle.getTitleStatus(),
            vehicle.getCarfaxUrl(),
            vehicle.getStatus(),
            vehicle.getLocation(),
            vehicle.getPurchasePrice(),
            vehicle.getListPrice(),
            vehicle.getMarketPrice(),
            vehicle.getNotes(),
            vehicle.getAcquisitionChannel(),
            vehicle.getPurchaseDate(),
            vehicle.getExpectedReadyDate(),
            vehicle.getDocuments(),
            vehicle.getReservation(),
            primaryImageUrl,
            vehicle.getCreatedAt(),
            vehicle.getUpdatedAt(),
            vehicle.getCreatedBy(),
            vehicle.getUpdatedBy(),
            packages
        );
    }

    public record VehicleSearchCriteria(
        String vin,
        String manufacture,
        String model,
        Integer yearMin,
        Integer yearMax,
        java.math.BigDecimal priceMin,
        java.math.BigDecimal priceMax,
        Integer mileageMin,
        Integer mileageMax,
        String color,
        String status,
        String conditionGrade,
        List<UUID> packageIds,
        String keyword
    ) {}
}
