package com.itranswarp.exchange.enums;

public enum OrderStatus {
    PENDING(false),
    PARTIAL_COMPLETE(false),
    COMPLETE(true),
    PARTIAL_CANCEL(true),
    CANCELLED(true);

    public final boolean isDone;

    OrderStatus(boolean isDone) {
        this.isDone = isDone;
    }
}
