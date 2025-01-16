package com.dailycodework.dreamshops.dto;

import com.dailycodework.dreamshops.model.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private String description;
    private Category category;
    private List<ImageDto> images;
}
