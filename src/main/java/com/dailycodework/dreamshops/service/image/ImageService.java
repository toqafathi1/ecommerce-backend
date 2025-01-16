package com.dailycodework.dreamshops.service.image;

import com.dailycodework.dreamshops.dto.ImageDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Image;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.repository.ImageRepository;
import com.dailycodework.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;
   private final static Set<String>  ALLOWED_FILE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id));
    }

    @Transactional
    @Override
    public void deleteImageById(Long id) {
       if(!imageRepository.existsById(id)){
           throw new ResourceNotFoundException("No image found with id: " + id);
       }
       imageRepository.deleteById(id);
    }

    @Transactional
    @Override
    public List<ImageDto> saveImages( Long productId,   List<MultipartFile> files) {
        Product product = productService.getProductById(productId);
        if(product == null){
            throw new ResourceNotFoundException("Product not found with Id " + productId);
        }
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : files) {
            validateFileType(file.getContentType());
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

               Image savedImage = imageRepository.save(image);
               savedImage.setDownloadUrl("/api/v1/images/image/download/" +savedImage.getId());
               imageRepository.save(savedImage);


               ImageDto imageDto = new ImageDto();
               imageDto.setId(savedImage.getId());
               imageDto.setFileName(savedImage.getFileName());
               imageDto.setDownloadUrl(savedImage.getDownloadUrl());
               savedImageDto.add(imageDto);

            }   catch(IOException | SQLException e){
                throw new RuntimeException( "Error saving image: "+e.getMessage() , e);
            }
        }
        return savedImageDto;
    }

    @Transactional
    @Override
    public void updateImage( MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        if(file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is empty");
        }
          validateFileType(file.getContentType());
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Error processing image: " +e.getMessage() , e);
        }
    }
    private void validateFileType(String fileType){
        if (fileType == null || !ALLOWED_FILE_TYPES.contains(fileType)) {
            throw new IllegalArgumentException("Invalid file type. Allowed types are: " + ALLOWED_FILE_TYPES);
        }
    }
}
