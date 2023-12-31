package com.itranswarp.exchange.enums;

public enum Direction {
    BUY(1), 
    SELL(0);

    public final int value;

    Direction(int value) {
        this.value = value;
    }

    /**
     * Get negate direction.
     */
    public Direction negate() {
        return this == BUY ? SELL : BUY;
    }

    public static Direction of(int intValue) {
        if (intValue == 1) {
            return BUY;
        }
        if (intValue == 0) {
            return SELL;
        }
        throw new IllegalArgumentException("Invalid Direction value.");
    }
}
