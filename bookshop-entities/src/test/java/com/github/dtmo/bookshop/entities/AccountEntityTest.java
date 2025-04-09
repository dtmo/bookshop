package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AccountEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(AccountEntity.class).verify();
    }
}
