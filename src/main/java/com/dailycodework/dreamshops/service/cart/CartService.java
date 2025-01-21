package com.dailycodework.dreamshops.service.cart;

import com.dailycodework.dreamshops.dto.CartDto;
import com.dailycodework.dreamshops.dto.CartItemDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.exceptions.UnauthorizedAccessException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.model.User;
import com.dailycodework.dreamshops.repository.CartItemRepository;
import com.dailycodework.dreamshops.repository.CartRepository;
import com.dailycodework.dreamshops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper ;
   private final UserRepository userRepository;

    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }


    @Transactional
    @Override
    public void clearCart(Long id) {
       Cart cart = getCart(id);
       cart.getItems().clear();
       cart.setTotalAmount(BigDecimal.ZERO);
       cartRepository.save(cart);

    }
    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

    @Override
    public Cart initializeNewCart(User user) {
        // if  user have cart return it else create new one
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user ID : " + userId));
    }

    @Override
    public CartDto convertToDto(Cart cart) {
        CartDto cartDto = modelMapper.map(cart , CartDto.class);
        Set<CartItemDto> cartItemDto = cart.getItems().stream()
                .map(cartItem -> modelMapper.map(cartItem , CartItemDto.class))
                .collect(Collectors.toSet());
        cartDto.setItems(cartItemDto);
        return cartDto ;
    }

    public  void validateCartOwnership(User user , Cart cart){
        if(user == null){
            throw new ResourceNotFoundException("User not found ");
        }
        if(!cart.getUser().getId().equals(user.getId())){
            throw new UnauthorizedAccessException("User does not have permission to a access this cart");
        }
    }

}
