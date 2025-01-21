package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.dto.OrderDto;
import com.dailycodework.dreamshops.model.Order;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
@EnableMethodSecurity(prePostEnabled = true)
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                                   @RequestParam String phoneNumber) {
        Long userId = ((User) userDetails).getId();
        Order order =  orderService.placeOrder(userId , phoneNumber);
        return ResponseEntity.ok(new ApiResponse("Order placed successfully", order));
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<ApiResponse> getAllOrders( @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "orderDate,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<OrderDto> orders = orderService.getAllOrders( pageable);
        return ResponseEntity.ok(new ApiResponse("All orders retrieved successfully", orders));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId , @AuthenticationPrincipal UserDetails userDetails) {
        Long authenticatedUserId =((User) userDetails).getId();
        if(!authenticatedUserId.equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Access denied" , null));
        }
            List<OrderDto> order = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Item Order Success!", order));

    }

    @PutMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> cancelOrder( @PathVariable Long orderId,  @AuthenticationPrincipal UserDetails userDetails ) {
        Long authenticatedUserId =((User) userDetails).getId();
        if(!authenticatedUserId.equals(orderId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse("Access denied" , null));
        }
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new ApiResponse("Order canceled successfully !", null));
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
