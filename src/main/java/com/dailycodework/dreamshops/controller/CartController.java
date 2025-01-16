package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.dto.CartDto;
import com.dailycodework.dreamshops.exceptions.UnauthorizedAccessException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.cart.CartService;
import com.dailycodework.dreamshops.service.cart.ICartService;
import com.dailycodework.dreamshops.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final CartService cartService;
    private final UserService userService ;

    @GetMapping("/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getCart( @PathVariable Long cartId) {
           User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCart(cartId);
            cartService.validateCartOwnership(user , cart);

            CartDto cartDto = cartService.convertToDto(cart);
            return ResponseEntity.ok(new ApiResponse("Success", cartDto));
    }

    @DeleteMapping("/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> clearCart( @PathVariable Long cartId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getCart(cartId);
        cartService.validateCartOwnership(user , cart);
        cartService.clearCart(cartId);
        return ResponseEntity.ok(new ApiResponse("Clear Cart Success!", null));

    }

    @GetMapping("/{cartId}/total-price")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> getTotalAmount( @PathVariable Long cartId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getCart(cartId);
        cartService.validateCartOwnership(user, cart);
        BigDecimal totalPrice = cartService.getTotalPrice(cartId);
        return ResponseEntity.ok(new ApiResponse("Total Price", totalPrice));
    }
}
