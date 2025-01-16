package com.dailycodework.dreamshops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private BigDecimal unitPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem(Order order, Product product, int quantity, BigDecimal unitePrice , BigDecimal multiply) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitePrice;

    }

    public BigDecimal getTotalPrice() {
       if(unitPrice == null || quantity <= 0 ){
           return BigDecimal.ZERO;
       }
       return unitPrice.multiply(new BigDecimal(quantity));
    }
}
