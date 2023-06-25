package com.itranswarp.exchange.assets;

public enum Transfer {
    // AVAILABLE_TO_FROZEN means transfer from available to frozen.
    AVAILABLE_TO_AVAILABLE,
    // FROZEN_TO_FROZEN means transfer from frozen to frozen.
    AVAILABLE_TO_FROZEN,
    // FROZEN_TO_AVAILABLE means transfer from frozen to available.
    FROZEN_TO_AVAILABLE;
}
