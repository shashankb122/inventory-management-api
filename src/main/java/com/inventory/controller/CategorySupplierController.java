package com.inventory.controller;

import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.*;
import com.inventory.repository.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories")
@SecurityRequirement(name = "bearerAuth")
class CategoryController {

    private final CategoryRepository categoryRepo;

    CategoryController(CategoryRepository categoryRepo) { this.categoryRepo = categoryRepo; }

    static class CategoryRequest {
        @NotBlank public String name;
        public String description;
    }

    @GetMapping        public List<Category> list() { return categoryRepo.findAll(); }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) {
        return categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody CategoryRequest req) {
        var cat = new Category(); cat.setName(req.name); cat.setDescription(req.description);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryRepo.save(cat));
    }

    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        var cat = categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        cat.setName(req.name); cat.setDescription(req.description);
        return categoryRepo.save(cat);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        if (!categoryRepo.existsById(id)) throw new ResourceNotFoundException("Category not found: " + id);
        categoryRepo.deleteById(id);
        return Map.of("message", "Category deleted");
    }
}

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Suppliers")
@SecurityRequirement(name = "bearerAuth")
class SupplierController {

    private final SupplierRepository supplierRepo;

    SupplierController(SupplierRepository supplierRepo) { this.supplierRepo = supplierRepo; }

    static class SupplierRequest {
        @NotBlank public String name;
        @Email public String email;
        public String phone;
        public String address;
    }

    @GetMapping        public List<Supplier> list() { return supplierRepo.findAll(); }

    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Long id) {
        return supplierRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Supplier> create(@Valid @RequestBody SupplierRequest req) {
        var s = new Supplier(); s.setName(req.name); s.setEmail(req.email); s.setPhone(req.phone); s.setAddress(req.address);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierRepo.save(s));
    }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @Valid @RequestBody SupplierRequest req) {
        var s = supplierRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        s.setName(req.name); s.setEmail(req.email); s.setPhone(req.phone); s.setAddress(req.address);
        return supplierRepo.save(s);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        if (!supplierRepo.existsById(id)) throw new ResourceNotFoundException("Supplier not found: " + id);
        supplierRepo.deleteById(id);
        return Map.of("message", "Supplier deleted");
    }
}
