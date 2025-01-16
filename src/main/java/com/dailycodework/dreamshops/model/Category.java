package com.dailycodework.dreamshops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "category name cannot be blank ")
    @Size(max = 26 , message = "category name cannot exceeds 26 character ")
    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Product> products ;

    public Category(String name) {
        this.name = name;
    }

    public void removeProduct(Product product){
        this.products.remove(product);
        product.setCategory(null);
    }
}
