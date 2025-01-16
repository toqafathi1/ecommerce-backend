package com.dailycodework.dreamshops.model;

import com.dailycodework.dreamshops.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private LocalDateTime orderDate;
    @DecimalMin(value = "0.0" , inclusive = true , message = "total amount must be positive")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "order status cannot be null")
    private OrderStatus orderStatus;
    private LocalDateTime cancellationDate;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<OrderItem> orderItems = new HashSet<>();

    @ManyToOne
    @NotNull(message = "Order must be associated with a user")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToMany
    @JoinTable(name = "order_product" ,
    joinColumns = @JoinColumn(name = "order_id") ,
    inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> products =new ArrayList<>();
}
