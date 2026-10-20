package com.inventory;

import com.inventory.exception.ResourceNotFoundException;
import com.inventory.model.*;
import com.inventory.repository.*;
import com.inventory.service.ProductService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepo;
    @Mock CategoryRepository categoryRepo;
    @Mock SupplierRepository supplierRepo;
    @Mock StockMovementRepository movementRepo;

    @InjectMocks ProductService productService;

    private Product sampleProduct() {
        var p = new Product();
        // id is database-generated, set via reflection for testing
        try {
            var f = Product.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(p, 1L);
        } catch (Exception ignored) {}
        p.setName("Test Product");
        p.setUnitPrice(BigDecimal.valueOf(9.99));
        p.setStockQuantity(50);
        p.setLowStockThreshold(10);
        return p;
    }

    @Test
    void getById_found() {
        var product = sampleProduct();
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getById(1L);

        assertThat(result.getName()).isEqualTo("Test Product");
    }

    @Test
    void getById_notFound_throws() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void recordMovement_restock_increasesStock() {
        var product = sampleProduct(); // stock = 50
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenReturn(product);
        var movement = new StockMovement();
        movement.setStockBefore(50);
        movement.setStockAfter(70);
        when(movementRepo.save(any())).thenReturn(movement);

        var result = productService.recordMovement(1L, StockMovement.MovementType.RESTOCK, 20, "Delivery");

        assertThat(result.getStockAfter()).isEqualTo(70);
    }

    @Test
    void recordMovement_sale_decreasesStock() {
        var product = sampleProduct(); // stock = 50
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenReturn(product);
        var movement = new StockMovement();
        movement.setStockBefore(50);
        movement.setStockAfter(40);
        when(movementRepo.save(any())).thenReturn(movement);

        var result = productService.recordMovement(1L, StockMovement.MovementType.SALE, 10, "Online order");

        assertThat(result.getStockAfter()).isEqualTo(40);
    }

    @Test
    void delete_notFound_throws() {
        when(productRepo.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void product_isLowStock_whenBelowThreshold() {
        var product = sampleProduct();
        product.setStockQuantity(5);
        product.setLowStockThreshold(10);

        assertThat(product.isLowStock()).isTrue();
    }

    @Test
    void product_isNotLowStock_whenAboveThreshold() {
        var product = sampleProduct();
        product.setStockQuantity(50);
        product.setLowStockThreshold(10);

        assertThat(product.isLowStock()).isFalse();
    }
}