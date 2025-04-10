package com.product.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.constant.GeneralConstant;
import com.product.app.entity.Category;
import com.product.app.entity.Product;
import com.product.app.entity.ProductValueHistory;
import com.product.app.repository.CategoryRepository;
import com.product.app.repository.ProductRepository;
import com.product.app.repository.ProductValueHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductValueHistoryService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductValueHistoryRepository productValueHistoryRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    CategoryRepository categoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    @Scheduled(cron = "0 0 0 * * ?")   // jalan setiap hari
    public void updateInvestmentProductPrices() {
        List<Product> allProducts = productRepository.findAll();

        for (Product product : allProducts) {
            Category category = categoryRepository.findById(product.getCategoryId())
                    .orElse(null);

            if (category == null) continue;

            if ("Investasi".equalsIgnoreCase(category.getCategoryType())) {
                double oldValue = product.getProductValue();

                // Generate a random percentage change between -2% and +2%
                double randomPercentageChange = (Math.random() * 0.04) - 0.02;  // This gives a value between -0.02 and +0.02

                double newValue = oldValue * (1 + randomPercentageChange); // Apply the random percentage change

                // Simpan perubahan harga dalam histori
                ProductValueHistory productValueHistory = new ProductValueHistory();
                productValueHistory.setProduct(product);
                productValueHistory.setOldValue(oldValue);
                productValueHistory.setValue(newValue); // still saved for compatibility
                productValueHistory.setDateChanged(new Date());
                productValueHistoryRepository.save(productValueHistory);

                // Update harga produk
                product.setProductValue(newValue);
                productRepository.save(product);

                // Audit trail
                try {
                    String beforeUpdate = String.format("Product ID: %s, Old Value: %.2f", product.getProductId(), oldValue);
                    String afterUpdate = String.format("Product ID: %s, New Value: %.2f", product.getProductId(), newValue);

                    auditTrailsService.logsAuditTrails(
                            GeneralConstant.LOG_ACVITIY_SAVE_HISTORY_VALUE,
                            mapper.writeValueAsString(beforeUpdate),
                            mapper.writeValueAsString(afterUpdate),
                            "Insert Product History Save"
                    );
                } catch (Exception e) {
                    // Log or handle exception
                    e.printStackTrace();
                }
            }
        }
    }
}
