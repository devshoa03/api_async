package com.javatechie.facade;

import com.javatechie.dto.ProductDetailDTO;
import com.javatechie.entity.Inventory;
import com.javatechie.entity.Price;
import com.javatechie.entity.Product;
import com.javatechie.service.InventoryService;
import com.javatechie.service.PriceService;
import com.javatechie.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class ProductAsyncFacade {


    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private PriceService priceService;


    public CompletableFuture<Product> getProductById(Long productId) {
        return CompletableFuture.supplyAsync(() -> productService.findById(productId));
    }

    public CompletableFuture<Inventory> getInventoryByProductId(Long productId) {
        return CompletableFuture.supplyAsync(() -> inventoryService.getInventoryByProductId(productId));
    }

    public CompletableFuture<Price> getPriceByProductId(Long productId) {
        return CompletableFuture.supplyAsync(() -> priceService.getPriceByProductId(productId));
    }

    public ProductDetailDTO getProductDetails(Long productId) {
        // fetch all async
        CompletableFuture<Product> productFuture = getProductById(productId);
        CompletableFuture<Inventory> inventoryFuture = getInventoryByProductId(productId);
        CompletableFuture<Price> priceFuture = getPriceByProductId(productId);

        // wait for all futures to complete
        CompletableFuture.allOf(productFuture, inventoryFuture, priceFuture);

        // combine the result
        Product product = productFuture.join();
        Inventory inventory = inventoryFuture.join();
        Price price = priceFuture.join();

        // build and return
        return ProductDetailDTO.builder()
                .id(product.getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .availableQuantity(inventory.getAvailableQuantity())
                .price(price.getPrice())
                .status(product.getStatus())
                .build();
    }


}