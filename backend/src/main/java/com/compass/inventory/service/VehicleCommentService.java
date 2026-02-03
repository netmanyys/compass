package com.compass.inventory.service;

import com.compass.inventory.dto.VehicleCommentDtos;
import com.compass.inventory.entity.Vehicle;
import com.compass.inventory.entity.VehicleComment;
import com.compass.inventory.repository.VehicleCommentRepository;
import com.compass.inventory.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleCommentService {
    private final VehicleCommentRepository commentRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleCommentService(VehicleCommentRepository commentRepository, VehicleRepository vehicleRepository) {
        this.commentRepository = commentRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleCommentDtos.CommentResponse> list(UUID vehicleId) {
        List<VehicleComment> comments = vehicleId == null ? commentRepository.findAll() : commentRepository.findByVehicleId(vehicleId);
        return comments.stream().map(this::toResponse).toList();
    }

    public VehicleCommentDtos.CommentResponse create(VehicleCommentDtos.CommentRequest request, String username) {
        Vehicle vehicle = vehicleRepository.findById(request.vehicleId()).orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        VehicleComment comment = new VehicleComment();
        comment.setId(UUID.randomUUID());
        comment.setVehicle(vehicle);
        comment.setComment(request.comment());
        comment.setCreatedAt(OffsetDateTime.now());
        comment.setCreatedBy(username);
        return toResponse(commentRepository.save(comment));
    }

    public void delete(UUID id) {
        commentRepository.deleteById(id);
    }

    private VehicleCommentDtos.CommentResponse toResponse(VehicleComment comment) {
        return new VehicleCommentDtos.CommentResponse(
            comment.getId(),
            comment.getVehicle().getId(),
            comment.getComment(),
            comment.getCreatedAt(),
            comment.getCreatedBy()
        );
    }
}
