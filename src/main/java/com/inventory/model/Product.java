package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(unique = true, length = 50)
    private String sku;

    @NotNull @DecimalMin("0.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Min(0) @Column(nullable = false)
    private int stockQuantity = 0;

    @Min(0) @Column(nullable = false)
    private int lowStockThreshold = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Supplier supplier;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockMovement> stockMovements = new ArrayList<>();

    public Product() {}

    public boolean isLowStock() { return stockQuantity <= lowStockThreshold; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getSku() { return sku; }
    public void setSku(String s) { this.sku = s; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal p) { this.unitPrice = p; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int q) { this.stockQuantity = q; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int t) { this.lowStockThreshold = t; }
    public Category getCategory() { return category; }
    public void setCategory(Category c) { this.category = c; }
    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier s) { this.supplier = s; }
    public List<StockMovement> getStockMovements() { return stockMovements; }
}
