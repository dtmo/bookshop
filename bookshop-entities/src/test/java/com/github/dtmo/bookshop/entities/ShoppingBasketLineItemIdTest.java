package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ShoppingBasketLineItemIdTest {
    @Test
    void testEqualsHashCode() {
        final AccountEntity redAccountEntity = AccountEntity.builder()
                .id(1L)
                .name("Red Account")
                .build();
        final AccountEntity blueAccountEntity = AccountEntity.builder()
                .id(2L)
                .name("Blue Account")
                .build();
        EqualsVerifier.forClass(ShoppingBasketLineItemEntity.ShoppingBasketLineItemId.class)
                .withPrefabValues(AccountEntity.class, redAccountEntity, blueAccountEntity)
                .verify();
    }
}
