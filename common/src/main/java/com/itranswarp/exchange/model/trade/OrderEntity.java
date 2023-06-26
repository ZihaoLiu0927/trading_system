package com.itranswarp.exchange.model.trade;

import java.math.BigDecimal;

import com.itranswarp.exchange.enums.Direction;
import com.itranswarp.exchange.enums.OrderStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.itranswarp.exchange.model.support.EntitySupport;

@Entity
@Table(name = "orders")
public class OrderEntity {
    /*
     * Primary key is the order id.
     * Use wrapper type Long to avoid auto-boxing if caching the order id as cache key.
     */
    @Id
    @Column(nullable = false, updatable = false)
    public Long id;

    /**
     * sequenceId that create this order. ASC only.
     */
    @Column(nullable = false, updatable = false)
    public long sequenceId;

    /**
     * The user id that create this order.
     * Using wrapper type Long to avoid auto-boxing if caching the user id as cache key.
     */
    @Column(nullable = false, updatable = false)
    public Long userId;

    /**
     * The price of this order.
     */
    @Column(nullable = false, updatable = true, precision = EntitySupport.PRECISION, scale = EntitySupport.SCALE)
    public BigDecimal price;

    /**
     * The direction of this order.
     */
    @Column(nullable = false, updatable = false, length = EntitySupport.VAR_ENUM)
    public Direction direction;

    /**
     * The status of this order.
     */
    @Column(nullable = false, updatable = true, length = EntitySupport.VAR_ENUM) 
    public OrderStatus status;

    /**
     * The quantity of this order.
     */
    @Column(nullable = false, updatable = false, precision = EntitySupport.PRECISION, scale = EntitySupport.SCALE)
    public BigDecimal quantity;

    /*
     * The unfilled quantity of this order.
     */
    @Column(nullable = false, updatable = true, precision = EntitySupport.PRECISION, scale = EntitySupport.SCALE)
    public BigDecimal unfilledQuantity;

    /*
     * The created time of this order.
     */
    @Column(nullable = false, updatable = false)
    public long createdAt;

    /*
     * The updated time of this order.
     */
    @Column(nullable = false, updatable = false)
    public long updatedAt;

    /**
     * Update order status and unfilled quantity.
     * 
     * @param unfilledQuantity
     * @param status
     * @param updatedAt
     */
    public void updateOrder(BigDecimal unfilledQuantity, OrderStatus status, long updatedAt) {
        this.unfilledQuantity = unfilledQuantity;
        this.status = status;
        this.updatedAt = updatedAt;
    }
}
