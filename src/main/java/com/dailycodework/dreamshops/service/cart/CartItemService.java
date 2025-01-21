package com.dailycodework.dreamshops.service.cart;

import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.exceptions.UnauthorizedAccessException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.model.CartItem;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.repository.CartItemRepository;
import com.dailycodework.dreamshops.repository.CartRepository;
import com.dailycodework.dreamshops.service.product.IProductService;
import com.dailycodework.dreamshops.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService  implements ICartItemService{
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final CartService cartService;
    private final UserService userService ;

    @Transactional
    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart =cartService.getCart(cartId);
        cartService.validateCartOwnership(user , cart);

        Product product = productService.getProductById(productId);
        if(product == null){
            throw new ResourceNotFoundException("Product not found");
        }
        // check if the quantity is available
        if(quantity > product.getInventory()){
            throw new IllegalArgumentException("Not enough inventory for the product. available: " + product.getInventory());
        }
        //check if the product is already exist in the cart
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(()->{
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setUnitPrice(product.getPrice());
                    return newItem;
                });
        // update quantity
        cartItem.setQuantity(cartItem.getQuantity() + quantity);

        // calculate total price  and save to database
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);


    }
   @Transactional
    @Override
    public void removeItemFromCart(Long cartId, Long itemId) {
       User user = userService.getAuthenticatedUser();
       Cart cart =cartService.getCart(cartId);
       cartService.validateCartOwnership(user , cart);

        CartItem itemToRemove = getCartItem(cartId, itemId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void updateItemQuantity(Long cartId, Long itemId, int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart =cartService.getCart(cartId);
        cartService.validateCartOwnership(user , cart);

        Product product = productService.getProductById(itemId);
        if(quantity > product.getInventory()){
            throw new IllegalArgumentException("Not enough inventory for the product. available: " + product.getInventory());
        }
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in the cart "));
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(product.getPrice());
        cartItem.setTotalPrice();

        BigDecimal totalAmount = cart.getItems()
                .stream().map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        User user = userService.getAuthenticatedUser();
        Cart cart =cartService.getCart(cartId);
        cartService.validateCartOwnership(user , cart);

        return  cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }
}
