package com.reuse.reuse.controller;

import com.reuse.reuse.dto.PaginatedResponse;
import com.reuse.reuse.exception.ResourceNotFoundException;
import com.reuse.reuse.model.Resource;
import com.reuse.reuse.repository.ResourceRepository;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceRepository resourceRepository;

    public ResourceController(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    // =========================
    // CREATE (POST)
    // =========================
    @PostMapping
    public ResponseEntity<Resource> addResource(@Valid @RequestBody Resource resource) {
        Resource saved = resourceRepository.save(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // =========================
    // READ ALL (GET with pagination + filter)
    // =========================
    @GetMapping
    public PaginatedResponse<Resource> getAllResources(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String title,
            Pageable pageable) {

        Page<Resource> pageResult;

        if (category != null) {
            pageResult = resourceRepository.findByCategory(category, pageable);
        }
        else if (title != null) {
            pageResult = resourceRepository
                    .findByTitleContainingIgnoreCase(title, pageable);
        }
        else {
            pageResult = resourceRepository.findAll(pageable);
        }

        return new PaginatedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    // =========================
    // READ BY ID (GET)
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        return ResponseEntity.ok(resource);
    }

    // =========================
    // UPDATE (PUT)
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody Resource updatedResource) {

        Resource existing = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        existing.setTitle(updatedResource.getTitle());
        existing.setAuthor(updatedResource.getAuthor());
        existing.setCategory(updatedResource.getCategory());
        existing.setCondition(updatedResource.getCondition());
        existing.setPrice(updatedResource.getPrice());
        existing.setDescription(updatedResource.getDescription());

        Resource saved = resourceRepository.save(existing);

        return ResponseEntity.ok(saved);
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteResource(@PathVariable Long id) {

        Resource existing = resourceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Resource not found with id: " + id));

        resourceRepository.delete(existing);

        return ResponseEntity.ok("Resource deleted successfully");
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/";

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir + fileName);

        file.transferTo(destination);

        return fileName;
    }

}



