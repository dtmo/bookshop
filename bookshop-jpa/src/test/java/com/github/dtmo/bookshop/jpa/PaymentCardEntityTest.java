package com.github.dtmo.bookshop.jpa;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PaymentCardEntityTest {
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
        EqualsVerifier.forClass(PaymentCardEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(AccountEntity.class, redAccountEntity, blueAccountEntity)
                .verify();
    }
}
