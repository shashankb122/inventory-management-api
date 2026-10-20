package com.inventory.service;

import com.inventory.exception.BadRequestException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.*;
import com.inventory.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;
    private final StockMovementRepository movementRepo;

    public ProductService(ProductRepository productRepo, CategoryRepository categoryRepo,
                          SupplierRepository supplierRepo, StockMovementRepository movementRepo) {
        this.productRepo  = productRepo;
        this.categoryRepo = categoryRepo;
        this.supplierRepo = supplierRepo;
        this.movementRepo = movementRepo;
    }

    public Page<Product> getAll(String search, Long categoryId, Long supplierId, Pageable pageable) {
        if (search != null && !search.isBlank())
            return productRepo.findByNameContainingIgnoreCase(search, pageable);
        if (categoryId != null) return productRepo.findByCategoryId(categoryId, pageable);
        if (supplierId  != null) return productRepo.findBySupplierId(supplierId, pageable);
        return productRepo.findAll(pageable);
    }

    public Product getById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Transactional
    public Product create(String name, String description, String sku, BigDecimal unitPrice,
                          int stockQty, int lowStockThreshold, Long categoryId, Long supplierId) {
        var p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setSku(sku);
        p.setUnitPrice(unitPrice);
        p.setStockQuantity(stockQty);
        p.setLowStockThreshold(lowStockThreshold);
        if (categoryId != null)
            p.setCategory(categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId)));
        if (supplierId != null)
            p.setSupplier(supplierRepo.findById(supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId)));
        return productRepo.save(p);
    }

    @Transactional
    public Product update(Long id, String name, String description, String sku,
                          BigDecimal unitPrice, int lowStockThreshold, Long categoryId, Long supplierId) {
        var p = getById(id);
        if (name != null)        p.setName(name);
        if (description != null) p.setDescription(description);
        if (sku != null)         p.setSku(sku);
        if (unitPrice != null)   p.setUnitPrice(unitPrice);
        p.setLowStockThreshold(lowStockThreshold);
        if (categoryId != null)
            p.setCategory(categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId)));
        if (supplierId != null)
            p.setSupplier(supplierRepo.findById(supplierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId)));
        return productRepo.save(p);
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepo.existsById(id)) throw new ResourceNotFoundException("Product not found: " + id);
        productRepo.deleteById(id);
    }

    public List<Product> getLowStock() {
        return productRepo.findLowStockProducts();
    }

    @Transactional
    public StockMovement recordMovement(Long productId, StockMovement.MovementType type, int quantity, String note) {
        var product = getById(productId);
        int before = product.getStockQuantity();
        int after = switch (type) {
            case RESTOCK, RETURN -> before + quantity;
            case SALE            -> before - quantity;
            case ADJUSTMENT      -> quantity;
        };
        if (after < 0) throw new BadRequestException(
                "Insufficient stock. Current: " + before + ", requested: " + quantity);
        product.setStockQuantity(after);
        productRepo.save(product);

        var m = new StockMovement();
        m.setProduct(product);
        m.setType(type);
        m.setQuantity(quantity);
        m.setNote(note);
        m.setStockBefore(before);
        m.setStockAfter(after);
        return movementRepo.save(m);
    }

    public Page<StockMovement> getMovements(Long productId, Pageable pageable) {
        getById(productId);
        return movementRepo.findByProductId(productId, pageable);
    }
}
