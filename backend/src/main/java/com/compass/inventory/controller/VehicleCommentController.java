package com.compass.inventory.controller;

import com.compass.inventory.dto.VehicleCommentDtos;
import com.compass.inventory.service.VehicleCommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicle-comments")
public class VehicleCommentController {
    private final VehicleCommentService commentService;

    public VehicleCommentController(VehicleCommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<VehicleCommentDtos.CommentResponse> list(@RequestParam(required = false) UUID vehicleId) {
        return commentService.list(vehicleId);
    }

    @PostMapping
    public VehicleCommentDtos.CommentResponse create(@Valid @RequestBody VehicleCommentDtos.CommentRequest request) {
        return commentService.create(request, currentUsername());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        commentService.delete(id);
    }

    private String currentUsername() {
        return org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
