package com.compass.inventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(length = 17, nullable = false, unique = true)
    private String vin;

    @Column(nullable = false)
    private String manufacture;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String color;

    private String trim;

    @Column(name = "body_type", nullable = false)
    private String bodyType;

    private String drivetrain;
    private String engine;
    private String transmission;
    @Column(name = "fuel_type")
    private String fuelType;

    @Column(nullable = false)
    private int mileage;

    @Column(name = "condition_grade", nullable = false)
    private String conditionGrade;

    @Column(name = "title_status", nullable = false)
    private String titleStatus;

    @Column(name = "carfax_url", columnDefinition = "text")
    private String carfaxUrl;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String location;

    @Column(name = "purchase_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "list_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal listPrice;

    @Column(name = "market_price", precision = 12, scale = 2)
    private BigDecimal marketPrice;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "acquisition_channel")
    private String acquisitionChannel;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "expected_ready_date")
    private LocalDate expectedReadyDate;

    @Column(columnDefinition = "text")
    private String documents;

    @Column(columnDefinition = "text")
    private String reservation;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_packages",
        joinColumns = @JoinColumn(name = "vehicle_id"),
        inverseJoinColumns = @JoinColumn(name = "package_id")
    )
    private Set<PackageEntity> packages = new HashSet<>();

    public Vehicle() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getManufacture() { return manufacture; }
    public void setManufacture(String manufacture) { this.manufacture = manufacture; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getTrim() { return trim; }
    public void setTrim(String trim) { this.trim = trim; }
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    public String getDrivetrain() { return drivetrain; }
    public void setDrivetrain(String drivetrain) { this.drivetrain = drivetrain; }
    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }
    public String getConditionGrade() { return conditionGrade; }
    public void setConditionGrade(String conditionGrade) { this.conditionGrade = conditionGrade; }
    public String getTitleStatus() { return titleStatus; }
    public void setTitleStatus(String titleStatus) { this.titleStatus = titleStatus; }
    public String getCarfaxUrl() { return carfaxUrl; }
    public void setCarfaxUrl(String carfaxUrl) { this.carfaxUrl = carfaxUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }
    public BigDecimal getMarketPrice() { return marketPrice; }
    public void setMarketPrice(BigDecimal marketPrice) { this.marketPrice = marketPrice; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getAcquisitionChannel() { return acquisitionChannel; }
    public void setAcquisitionChannel(String acquisitionChannel) { this.acquisitionChannel = acquisitionChannel; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    public LocalDate getExpectedReadyDate() { return expectedReadyDate; }
    public void setExpectedReadyDate(LocalDate expectedReadyDate) { this.expectedReadyDate = expectedReadyDate; }
    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }
    public String getReservation() { return reservation; }
    public void setReservation(String reservation) { this.reservation = reservation; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Set<PackageEntity> getPackages() { return packages; }
    public void setPackages(Set<PackageEntity> packages) { this.packages = packages; }
}
