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

@Component
@Slf4j
public class ProductSyncFacade {

    @Autowired
    private ProductService productService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private PriceService priceService;


    /**
     * Phương thức này sẽ thực thi trong một luồng duy nhất sẽ gây ra chậm
     * @param productId
     * @return
     */
    public ProductDetailDTO getProductDetails(Long productId) {
        log.info("Sync facade for getting product details for the product id {}", productId);
        // fetch product
        Product product = productService.findById(productId);

        // fetch the price
        Price price = priceService.getPriceByProductId(productId);

        // fetch the inventory
        Inventory inventory = inventoryService.getInventoryByProductId(productId);

        // combine result
        return new ProductDetailDTO(
                product.getId(),
                product.getCategory().getName(),
                product.getName(),
                product.getDescription(),
                inventory.getAvailableQuantity(),
                price.getPrice(),
                product.getStatus()
        );
    }

}
