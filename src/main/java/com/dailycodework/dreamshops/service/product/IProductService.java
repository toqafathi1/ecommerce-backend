package com.dailycodework.dreamshops.service.product;
import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);

    Page<ProductDto> getAllProducts(Pageable pageable);
    Page<ProductDto>  getProductsByCategory(String category , Pageable pageable);
    Page<ProductDto> getProductsByBrand(String brand , Pageable pageable);

    Page<ProductDto> getProductsByCategoryAndBrand(String category, String brand, Pageable pageable);
    Page<ProductDto> getProductsByName(String name, Pageable pageable);
    Page<ProductDto> getProductsByBrandAndName(String brand, String name, Pageable pageable);

    Long countProductsByBrandAndName(String brand, String name);

//    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

}
