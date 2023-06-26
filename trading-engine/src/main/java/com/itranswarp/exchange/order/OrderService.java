package com.itranswarp.exchange.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itranswarp.exchange.assets.AssetService;
import com.itranswarp.exchange.enums.AssetEnum;
import com.itranswarp.exchange.enums.Direction;
import com.itranswarp.exchange.enums.OrderStatus;
import com.itranswarp.exchange.model.trade.OrderEntity;

@Component
public class OrderService {

    @Autowired 
    final AssetService assetService;

    final ConcurrentHashMap<Long, ConcurrentHashMap<Long, OrderEntity>> userOrders = new ConcurrentHashMap<>();
    final ConcurrentHashMap<Long, OrderEntity> avtiveOrders = new ConcurrentHashMap<>();

    public OrderService(AssetService assetService) {
        this.assetService = assetService;
    }

    // Create order, return null if failed; otherwise return order.
    public OrderEntity createOrder(Long userId, Long orderId, long sequenceId, long ts, BigDecimal price, Direction direction, BigDecimal quantity) {
        switch(direction) {
            case BUY -> {
                if (!assetService.tryFreeze(userId, AssetEnum.USD, price.multiply(quantity))) {
                    return null;
                }
            }
            case SELL -> {
                if (!assetService.tryFreeze(userId, AssetEnum.BTC, quantity)) {
                    return null;
                }
            }
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        }

        OrderEntity order = new OrderEntity();
        order.id = orderId;
        order.sequenceId = sequenceId;
        order.userId = userId;
        order.price = price;
        order.direction = direction;
        order.status = OrderStatus.PENDING;
        order.createdAt = order.updatedAt = ts;
        order.quantity = quantity;
        order.unfilledQuantity = quantity;
        ConcurrentHashMap<Long, OrderEntity> userOrder = userOrders.get(userId);
        if (userOrder == null) {
            userOrder = new ConcurrentHashMap<Long, OrderEntity>();
            userOrders.put(orderId, userOrder);
        }  
        userOrder.put(orderId, order);
        avtiveOrders.put(orderId, order);
        return order;
    }

    // delete order after filled or cancelled.
    public void removeOrder(Long orderId) {
        OrderEntity removed = this.avtiveOrders.remove(orderId);
        if (removed == null) {
            throw new IllegalArgumentException("Active order not found by orderId: " + orderId);
        }
        ConcurrentHashMap<Long, OrderEntity> userOrder = userOrders.get(removed.userId); 
        if (userOrder == null) {
            throw new IllegalArgumentException("User orders not found by userId: " + orderId);
        }
        if (userOrder.remove(orderId) == null) {
            throw new IllegalArgumentException("Order not found by orderId in user orders: " + orderId);
        }
    }

    public OrderEntity getOrder(Long orderId) {
        return this.avtiveOrders.get(orderId);
    }

    public ConcurrentMap<Long, OrderEntity> getUserOrders(Long userId) {
        return this.userOrders.get(userId);
    }

    public ConcurrentHashMap<Long, OrderEntity> getActiveOrders() {
        return this.avtiveOrders;
    }

    public void debug() {
        System.out.println("---------- active orders >>>>>>>>>>");
        List<OrderEntity> orders =  new ArrayList<>(this.avtiveOrders.values());
        for (OrderEntity order : orders) {
            System.out.println("  orderId=" + order.id + " type=" + order.direction + " userId=" + order.userId
                     + " price: " + order.price + " unfilled: " + order.unfilledQuantity 
                     + " total quantity: " + order.quantity + " sequenceId: " + order.sequenceId);
        }
        System.out.println(">>>>>>>>>> active orders ----------"); 
    }
}
