package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AuthorEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(AuthorEntity.class).suppress(Warning.SURROGATE_KEY).verify();
    }
}
