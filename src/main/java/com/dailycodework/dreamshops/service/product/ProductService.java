package com.dailycodework.dreamshops.service.product;

import com.dailycodework.dreamshops.dto.ImageDto;
import com.dailycodework.dreamshops.dto.ProductDto;
import com.dailycodework.dreamshops.exceptions.AlreadyExistsException;
import com.dailycodework.dreamshops.exceptions.NoChangesDetectedException;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Category;
import com.dailycodework.dreamshops.model.Image;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.repository.CategoryRepository;
import com.dailycodework.dreamshops.repository.ImageRepository;
import com.dailycodework.dreamshops.repository.ProductRepository;
import com.dailycodework.dreamshops.request.AddProductRequest;
import com.dailycodework.dreamshops.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Transactional
    @Override
    public Product addProduct(AddProductRequest request) {
        if(productExists(request.getName(),request.getBrand())){
            throw new AlreadyExistsException(request.getName() + " already exists");
        }
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }
  private boolean productExists(String name , String brand){
        return productRepository.existsByNameAndBrand(name,brand);
  }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    @Transactional
    @Override
    public void deleteProductById(Long id) {
       Product product = productRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
       if(!product.getOrders().isEmpty()){
           throw new IllegalStateException("Cannot delete product: It is associated with one or more orders.");
       }
       Category category =product.getCategory();
       if (category !=null){
           category.removeProduct(product); // remove product from category
           categoryRepository.save(category); // save updated category
       }
       productRepository.delete(product);

    }
    @Transactional
    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> {
                    if(!existingProduct.getOrders().isEmpty()){
                        throw new IllegalStateException("Cannot delete product: It is associated with one or more orders.");
                    }
                    return updateExistingProduct(existingProduct,request);
                })
                .map(productRepository :: save)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }
    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        if(category ==null){
            throw new ResourceNotFoundException("Category Not found " + request.getCategory().getName());
        }
        existingProduct.setCategory(category);
        return  existingProduct;

    }
    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
       Page<Product> products = productRepository.findAll(pageable);
       if (products.isEmpty()){
           throw new ResourceNotFoundException("There are no products added yet!");
       }
       return products.map(this::convertToDto);
    }
    @Override
    public Page<ProductDto>  getProductsByCategory(String category , Pageable pageable) {
        Page <Product> products = productRepository.findByCategoryName(category,pageable);
        return products.map(this::convertToDto) ;
    }

    @Override
    public Page<ProductDto>  getProductsByBrand(String brand , Pageable pageable) {
        Page <Product> products = productRepository.findByBrand(brand,pageable);
        return products.map(this::convertToDto) ;
    }

    @Override
    public Page<ProductDto>  getProductsByCategoryAndBrand(String category, String brand , Pageable pageable) {
        Page <Product> products = productRepository.findByCategoryAndBrand(category,brand , pageable);
        return products.map(this::convertToDto) ;
    }

    @Override
    public Page<ProductDto>  getProductsByName(String name , Pageable pageable) {
        Page <Product> products = productRepository.findProductsByName(name,pageable);
        return products.map(this::convertToDto) ;
    }

    @Override
    public Page<ProductDto>  getProductsByBrandAndName(String brand, String name , Pageable pageable) {
        Page <Product> products = productRepository.findProductsByBrandAndName(brand, name ,pageable);
        return products.map(this::convertToDto) ;

    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

//    @Override
//    public List<ProductDto> getConvertedProducts(List<Product> products) {
//      return products.stream().map(this::convertToDto).toList();
//    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}
