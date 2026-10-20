package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    public enum MovementType { RESTOCK, SALE, ADJUSTMENT, RETURN }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"stockMovements", "hibernateLazyInitializer", "handler"})
    private Product product;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MovementType type;

    @Min(1) @Column(nullable = false)
    private int quantity;

    @Column(length = 255)
    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private int stockBefore;

    @Column(nullable = false)
    private int stockAfter;

    public StockMovement() {}

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product p) { this.product = p; }
    public MovementType getType() { return type; }
    public void setType(MovementType t) { this.type = t; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; }
    public String getNote() { return note; }
    public void setNote(String n) { this.note = n; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getStockBefore() { return stockBefore; }
    public void setStockBefore(int s) { this.stockBefore = s; }
    public int getStockAfter() { return stockAfter; }
    public void setStockAfter(int s) { this.stockAfter = s; }
}
