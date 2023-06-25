package com.itranswarp.exchange.assets;

import java.math.BigDecimal;

public class Asset {
    // available is the amount of the asset that is available to trade
    BigDecimal available;
    // frozen is the amount of the asset that is frozen in open orders
    BigDecimal frozen;

    public Asset() {
        this.available = BigDecimal.ZERO;
        this.frozen = BigDecimal.ZERO;
    }

    public Asset(BigDecimal available, BigDecimal frozen) {
        this.available = available;
        this.frozen = frozen;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    public BigDecimal getTotal() {
        return available.add(frozen);
    }

    public String toString() {
        return String.format("[available=%04.2f, frozen=%02.2f]", available, frozen);
    }
}
