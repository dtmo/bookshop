package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AccountEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(AccountEntity.class).suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(CustomerEntity.class,
                        CustomerEntity.builder().id(1L).name("Red").build(),
                        CustomerEntity.builder().id(2L).name("Blue").build())
                .verify();
    }
}
