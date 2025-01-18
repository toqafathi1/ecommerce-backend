package com.dailycodework.dreamshops.controller;


import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.exceptions.NoChangesDetectedException;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.product.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
@EnableMethodSecurity(prePostEnabled = true)
public class ProductController {
    private final IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String [] sort
    ) {
           Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));
           Page<ProductDto> products = productService.getAllProducts(pageable);
            return  ResponseEntity.ok(new ApiResponse("success", products));
    }
    @GetMapping("/id/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            return  ResponseEntity.ok(new ApiResponse("success", productDto));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody AddProductRequest product) {
            Product theProduct = productService.addProduct(product);
            ProductDto productDto = productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("Product added successfully ", productDto));

    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<ApiResponse> updateProduct(@Valid @RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
        try {
            Product theProduct = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(theProduct);
            return ResponseEntity.ok(new ApiResponse("Product Updated successfully!", productDto));
        }catch (NoChangesDetectedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Product Deleted successfully!", productId));
    }

    @GetMapping("/search/by-brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName,
                                                                @RequestParam String productName,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "name,asc") String[] sort) {
        Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));

        Page<ProductDto> products = productService.getProductsByBrandAndName(brandName , productName , pageable);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }
            return  ResponseEntity.ok(new ApiResponse("success", products));

    }

    @GetMapping("/search/by-category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category,
                                                                    @RequestParam String brand,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "name,asc") String[] sort){
        Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));
        Page<ProductDto> products = productService.getProductsByCategoryAndBrand(category, brand , pageable);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }
            return  ResponseEntity.ok(new ApiResponse("success", products));

    }

    @GetMapping("/search/by-name/{name}")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "name,asc") String[] sort){

        Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));
        Page<ProductDto> products = productService.getProductsByName(name , pageable);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }
            return  ResponseEntity.ok(new ApiResponse("success", products));

    }

    @GetMapping("/search/by-brand")
    public ResponseEntity<ApiResponse> getProductByBrand(@RequestParam String brand,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "name,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));
        Page<ProductDto> products = productService.getProductsByBrand(brand , pageable);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }
            return  ResponseEntity.ok(new ApiResponse("success", products));

    }

    @GetMapping("/search/by-category/{category}")
    public ResponseEntity<ApiResponse> getProductByCategory(@PathVariable String category,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "name,asc") String[] sort) {
            Pageable pageable = PageRequest.of(page , size , Sort.by(createSortOrder(sort)));
            Page<ProductDto> products = productService.getProductsByCategory(category , pageable);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }
            return  ResponseEntity.ok(new ApiResponse("success", products));
    }

    @GetMapping("/count/by-brand/and-name")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand, @RequestParam String name) {

            var productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product count!", productCount));

    }


    private List<Sort.Order> createSortOrder(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort[0].contains(",")) {
            // Multiple sorting criteria
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // Single sorting criteria
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }
        return orders;
    }

    private Sort.Direction getSortDirection(String direction) {
        return direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}
