package com.itranswarp.exchange.assets;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.itranswarp.exchange.enums.AssetEnum;

public class AssetServiceTest {

    static final Long DEBT = 1L;
    static final Long USER_A = 2000L;
    static final Long USER_B = 3000L;
    static final Long USER_C = 4000L;

    AssetService assetService;

    @BeforeEach
    public void setUp() {
        assetService = new AssetService();
        init();
    }

    @AfterEach
    public void tearDown() {
        verify();
    }


    @Test
    void testTryTransfer() {
        // A available USD -> B available USD
        boolean t1 = assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD, new BigDecimal("200"), true);
        assertTrue(t1);
        assertEquals(800, assetService.getAsset(USER_A, AssetEnum.USD).available.longValue());
        assertEquals(1200, assetService.getAsset(USER_B, AssetEnum.USD).available.longValue());

        // A available BTC -> B available BTC
        boolean t2 = assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.BTC, new BigDecimal("10"), true);
        assertTrue(t2);
        assertEquals(50, assetService.getAsset(USER_A, AssetEnum.BTC).available.longValue());
        assertEquals(10, assetService.getAsset(USER_B, AssetEnum.BTC).available.longValue()); 

        // Transaction fail: A available BTC -> B available BTC
        boolean t3 = assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, USER_A, USER_B, AssetEnum.BTC, new BigDecimal("51"), true);
        assertFalse(t3);
        assertEquals(50, assetService.getAsset(USER_A, AssetEnum.BTC).available.longValue());
        assertEquals(10, assetService.getAsset(USER_B, AssetEnum.BTC).available.longValue());
    }

    @Test
    void testTransfer() {
        // A available USD -> A freeze USD
        assetService.transfer(Transfer.AVAILABLE_TO_FROZEN, USER_A, USER_A, AssetEnum.USD, new BigDecimal("500"));
        assertEquals(500, assetService.getAsset(USER_A, AssetEnum.USD).available.longValue());
        assertEquals(500, assetService.getAsset(USER_A, AssetEnum.USD).frozen.longValue());

        // A available BTC -> A freeze BTC
        assetService.transfer(Transfer.AVAILABLE_TO_FROZEN, USER_A, USER_A, AssetEnum.BTC, new BigDecimal("20"));
        assertEquals(40, assetService.getAsset(USER_A, AssetEnum.BTC).available.longValue());
        assertEquals(20, assetService.getAsset(USER_A, AssetEnum.BTC).frozen.longValue());

        // A freeze USD -> B available USD
        assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD, new BigDecimal("100"));
        assertEquals(400, assetService.getAsset(USER_A, AssetEnum.USD).frozen.longValue()); 
        assertEquals(1100, assetService.getAsset(USER_B, AssetEnum.USD).available.longValue());

        // A freeze BTC -> C available BTC
        assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, USER_A, USER_C, AssetEnum.BTC, new BigDecimal("10"));
        assertEquals(10, assetService.getAsset(USER_A, AssetEnum.BTC).frozen.longValue());
        assertEquals(10, assetService.getAsset(USER_C, AssetEnum.BTC).available.longValue());

        // Transaction fail: A freeze USD -> B available USD
        assertThrows(RuntimeException.class, () -> {
            assetService.transfer(Transfer.FROZEN_TO_AVAILABLE, USER_A, USER_B, AssetEnum.USD, new BigDecimal("401"));
        });
    }

    @Test
    void testTryFreeze() {
        // A available USD -> A freeze USD
        assetService.tryFreeze(USER_A, AssetEnum.USD, new BigDecimal("100"));
        assertEquals(900, assetService.getAsset(USER_A, AssetEnum.USD).available.longValue());
        assertEquals(100, assetService.getAsset(USER_A, AssetEnum.USD).frozen.longValue());

        // Transaction fail: A available BTC -> A freeze BTC
        assetService.tryFreeze(USER_A, AssetEnum.BTC, new BigDecimal("61"));
        assertEquals(60, assetService.getAsset(USER_A, AssetEnum.BTC).available.longValue());
        assertEquals(0, assetService.getAsset(USER_A, AssetEnum.BTC).frozen.longValue());
    }

    @Test
    void testUnfreeze() {
        // B available USD -> B freeze USD
        assetService.tryFreeze(USER_B, AssetEnum.USD, new BigDecimal("400"));
        assertEquals(600, assetService.getAsset(USER_B, AssetEnum.USD).available.longValue());
        assetService.unfreeze(USER_B, AssetEnum.USD, new BigDecimal("200"));
        assertEquals(800, assetService.getAsset(USER_B, AssetEnum.USD).available.longValue());

        assertThrows(RuntimeException.class, () -> {
            assetService.unfreeze(USER_B, AssetEnum.USD, new BigDecimal("201"));
        });
    }

    void init() {
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_A, AssetEnum.USD, new BigDecimal("1000"), false);
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_B, AssetEnum.USD, new BigDecimal("1000"), false);
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_C, AssetEnum.USD, new BigDecimal("1000"), false);
        assetService.tryTransfer(Transfer.AVAILABLE_TO_AVAILABLE, DEBT, USER_A, AssetEnum.BTC, new BigDecimal("60"), false);
        assertEquals(-3000L, assetService.getAsset(DEBT, AssetEnum.USD).available.longValue());
        assertEquals(-60L, assetService.getAsset(DEBT, AssetEnum.BTC).available.longValue());
    }

    void verify() {
        BigDecimal totalUSD = BigDecimal.ZERO;
        BigDecimal totalBTC = BigDecimal.ZERO;
        for (Long userId : assetService.getUerAssets().keySet()) {
            var assets = assetService.getAssets(userId);
            if (assets.get(AssetEnum.USD) != null) {
                totalUSD = totalUSD.add(assets.get(AssetEnum.USD).available).add(assets.get(AssetEnum.USD).frozen);
            }
            if (assets.get(AssetEnum.BTC) != null) {
                totalBTC = totalBTC.add(assets.get(AssetEnum.BTC).available).add(assets.get(AssetEnum.BTC).frozen);
            }
        }
        assertEquals(0L, totalUSD.longValue());
        assertEquals(0L, totalBTC.longValue());
    }

}
