package com.dailycodework.dreamshops.model;

import com.dailycodework.dreamshops.validation.AllowedImageFileType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "File name cannot be blank")
    private String fileName;

    @Column(nullable = false)
    @NotBlank(message = "File type cannot be blank")
    @AllowedImageFileType
    private String fileType;

    @Lob
    @NotNull(message = "Image blob cannot be null")
    private Blob image;

    @NotBlank(message = "downloadUrl cannot be blank")
    private String downloadUrl ;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    @NotNull(message = "product cannot be null")
    private Product product;

}
