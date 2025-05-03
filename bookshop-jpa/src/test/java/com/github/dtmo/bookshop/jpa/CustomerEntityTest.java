package com.github.dtmo.bookshop.jpa;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class CustomerEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(CustomerEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(AccountEntity.class,
                        AccountEntity.builder().id(1L).name("Red Account").build(),
                        AccountEntity.builder().id(2L).name("Blue Account").build())
                .verify();
    }
}
