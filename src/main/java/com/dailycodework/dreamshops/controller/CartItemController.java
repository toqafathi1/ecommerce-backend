package com.dailycodework.dreamshops.controller;

import com.dailycodework.dreamshops.dto.CartDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.cart.CartItemService;
import com.dailycodework.dreamshops.service.cart.ICartItemService;
import com.dailycodework.dreamshops.service.cart.ICartService;
import com.dailycodework.dreamshops.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cart-items")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

    @PostMapping("/item")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId,
                                                     @Valid @RequestParam Integer quantity) {
        // retrieve authenticated users
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.initializeNewCart(user);

        cartItemService.addItemToCart(cart.getId(), productId, quantity);
        Cart updatedCart = cartService.getCart(cart.getId());
        CartDto cartDto = cartService.convertToDto(updatedCart);
        return ResponseEntity.ok(new ApiResponse("Add Item Success", cartDto));

    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId) {

            cartItemService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok(new ApiResponse("Remove Item Success", null));

    }

    @PutMapping("/{cartId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                          @PathVariable Long itemId,
                                                          @Valid @RequestParam Integer quantity) {
        cartItemService.updateItemQuantity(cartId, itemId, quantity);
        return ResponseEntity.ok(new ApiResponse("Update Item Success", null));

    }
}