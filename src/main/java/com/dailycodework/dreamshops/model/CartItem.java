package com.dailycodework.dreamshops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "quantity cannot be null")
    @Min(value = 1 , message = "quantity must be at least one")
    @Positive(message = " quantity must be greater than zero")
    private int quantity;

    @NotNull(message = "Unit price cannot be null")
    @Positive(message = "Unit price must be greater than zero")
    private BigDecimal unitPrice;


    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be greater than zero")
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @NotNull(message = "product cannot be null")
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setTotalPrice() {
        this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
    }


    
}
