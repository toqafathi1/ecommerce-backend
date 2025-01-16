package com.dailycodework.dreamshops.request;

import com.dailycodework.dreamshops.model.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private Long id;
    @NotBlank(message = "product name cannot be blank")
    private String name;
    @NotBlank(message = "product brand cannot be blank")
    private String brand;
    @NotNull(message = " price is required")
    @Positive
    private BigDecimal price;
    @NotNull(message ="Inventory is required")
    @Positive
    private int inventory;
    private String description;
    @NotNull(message="Category is required ")
    @Valid
    private Category category;
}
