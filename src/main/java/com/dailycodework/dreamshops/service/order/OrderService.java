package com.dailycodework.dreamshops.service.order;

import com.dailycodework.dreamshops.dto.OrderDto;
import com.dailycodework.dreamshops.enums.OrderStatus;
import com.dailycodework.dreamshops.exceptions.OrderNotFoundException;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.exceptions.UnauthorizedAccessException;
import com.dailycodework.dreamshops.model.*;
import com.dailycodework.dreamshops.repository.OrderRepository;
import com.dailycodework.dreamshops.repository.ProductRepository;
import com.dailycodework.dreamshops.repository.UserRepository;
import com.dailycodework.dreamshops.security.service.EmailService;
import com.dailycodework.dreamshops.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository ;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    @Transactional
    @Override
    public Order placeOrder(Long userId , String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found!")) ;

        if(!user.isEnabled()){
            throw new UnauthorizedAccessException("You need to verify your email first");
        }

        if(user.getAddresses().isEmpty()){
            throw new IllegalStateException("Cannot place an order: User does not have an address");
        }
        // Fetch cart
        Cart cart   = cartService.getCartByUserId(user.getId());
        if(cart.getItems().isEmpty()){
            throw new IllegalStateException("cannot place an order with an empty cart ");
        }
        // check inventory for all items in the cart
        for(CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            if( cartItem.getQuantity() > product.getInventory()){
                throw new IllegalArgumentException("Not enough inventory for product: " + product.getName());
            }
        }
        // create order and order items
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);

        //add products to order's product list
        for(OrderItem orderItem : orderItemList){
            order.getProducts().add(orderItem.getProduct());
        }
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        order.setPhoneNumber(phoneNumber);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());

        try {
            emailService.sendOrderConfirmation(savedOrder);
        } catch (MailException e) {
            throw new MailSendException("Failed to send order confirmation exception for order " + e);
        }
        return savedOrder;
    }

    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());
        return  order;
    }
     private List<OrderItem> createOrderItems(Order order, Cart cart) {
        // convert items in cart to oderItem object
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem: cart.getItems()){
            Product product = cartItem.getProduct();
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice(),
                    cartItem.getUnitPrice().multiply(new BigDecimal(cartItem.getQuantity()))

                    );
            orderItems.add(orderItem);
        }
     return orderItems;
     }

     private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return  orderItemList
                .stream()
                .map(OrderItem :: getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
     }

    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
         Page<Order> orders = orderRepository.findAll(pageable);
        if(orders.isEmpty()){
            throw new ResourceNotFoundException("No orders found ");
        }
        return orders.map(this::convertToDto);
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new ResourceNotFoundException("User with ID " + userId + " does not exist");
        }
        List<Order> orders = orderRepository.findByUserId(userId);
        if(orders.isEmpty()){
            throw new IllegalStateException("No orders found for this user ");
        }
        return  orders.stream().map(this :: convertToDto).toList();
    }

    @Transactional
    @Override
    public void cancelOrder(Long orderId) {
        Long userId = getAuthenticatedUserId();
        // fetch order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " doesn't exist"));
       //validate that the user own the order
        if(!order.getUser().getId().equals(userId)){
            throw new UnauthorizedAccessException("User does not have permission to cancel this order");
        }

    // Restore inventory for each order item
        List<Product> productsToUpdate = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setInventory(product.getInventory() + orderItem.getQuantity()); // Restore inventory
            productsToUpdate.add(product);
        }
        productRepository.saveAll(productsToUpdate);

    // update order status t canceled
     order.setOrderStatus(OrderStatus.CANCELLED);
     order.setCancellationDate(LocalDateTime.now());
     orderRepository.save(order);
    }

    private OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    private Long getAuthenticatedUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new RuntimeException("User is not authenticated");
        }
        return ((User) authentication.getPrincipal()).getId();
    }
    //TODO: handle other status of order
}
