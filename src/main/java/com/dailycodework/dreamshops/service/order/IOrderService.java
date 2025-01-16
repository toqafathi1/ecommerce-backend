package com.dailycodework.dreamshops.service.order;

import com.dailycodework.dreamshops.dto.OrderDto;
import com.dailycodework.dreamshops.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IOrderService {
    Order placeOrder();

    Page<OrderDto> getAllOrders(Pageable pageable);

    List<OrderDto> getUserOrders(Long userId);
    void cancelOrder(Long orderId );
}
