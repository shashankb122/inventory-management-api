package com.inventory.controller;

import com.inventory.model.*;
import com.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalogue and stock management")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    static class ProductRequest {
        @NotBlank public String name;
        public String description;
        public String sku;
        @NotNull @DecimalMin("0.00") public BigDecimal unitPrice;
        @Min(0) public int stockQuantity;
        @Min(0) public int lowStockThreshold = 10;
        public Long categoryId;
        public Long supplierId;
    }

    static class StockMovementRequest {
        @NotBlank public String type;
        @Min(1) public int quantity;
        public String note;
    }

    @Operation(summary = "List all products")
    @GetMapping
    public ResponseEntity<Page<Product>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc")  String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return ResponseEntity.ok(productService.getAll(search, categoryId, supplierId, PageRequest.of(page, size, sort)));
    }

    @Operation(summary = "Get all low-stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> lowStock() {
        return ResponseEntity.ok(productService.getLowStock());
    }

    @Operation(summary = "Get a product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                productService.create(req.name, req.description, req.sku, req.unitPrice,
                        req.stockQuantity, req.lowStockThreshold, req.categoryId, req.supplierId));
    }

    @Operation(summary = "Update a product")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.update(id, req.name, req.description, req.sku,
                req.unitPrice, req.lowStockThreshold, req.categoryId, req.supplierId));
    }

    @Operation(summary = "Delete a product")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @Operation(summary = "Record a stock movement (RESTOCK, SALE, ADJUSTMENT, RETURN)")
    @PostMapping("/{id}/stock")
    public ResponseEntity<StockMovement> recordMovement(@PathVariable Long id,
                                                         @Valid @RequestBody StockMovementRequest req) {
        StockMovement.MovementType type = StockMovement.MovementType.valueOf(req.type.toUpperCase());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.recordMovement(id, type, req.quantity, req.note));
    }

    @Operation(summary = "Get stock movement history for a product")
    @GetMapping("/{id}/stock")
    public ResponseEntity<Page<StockMovement>> getMovements(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getMovements(id,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
}
