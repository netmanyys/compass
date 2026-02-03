package com.compass.inventory.controller;

import com.compass.inventory.dto.ServiceRecordDtos;
import com.compass.inventory.service.ServiceRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-records")
public class ServiceRecordController {
    private final ServiceRecordService serviceRecordService;

    public ServiceRecordController(ServiceRecordService serviceRecordService) {
        this.serviceRecordService = serviceRecordService;
    }

    @GetMapping
    public List<ServiceRecordDtos.ServiceRecordResponse> list(@RequestParam(required = false) UUID vehicleId) {
        return serviceRecordService.list(vehicleId);
    }

    @GetMapping("/{id}")
    public ServiceRecordDtos.ServiceRecordResponse get(@PathVariable UUID id) {
        return serviceRecordService.get(id);
    }

    @PostMapping
    public ServiceRecordDtos.ServiceRecordResponse create(@Valid @RequestBody ServiceRecordDtos.ServiceRecordRequest request) {
        return serviceRecordService.create(request, currentUsername());
    }

    @PatchMapping("/{id}")
    public ServiceRecordDtos.ServiceRecordResponse update(@PathVariable UUID id, @Valid @RequestBody ServiceRecordDtos.ServiceRecordRequest request) {
        return serviceRecordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        serviceRecordService.delete(id);
    }

    private String currentUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
