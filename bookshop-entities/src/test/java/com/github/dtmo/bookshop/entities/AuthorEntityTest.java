package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class AuthorEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(AuthorEntity.class).verify();
    }
}
