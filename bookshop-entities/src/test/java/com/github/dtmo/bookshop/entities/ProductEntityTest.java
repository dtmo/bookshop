package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ProductEntityTest {
    @Test
    void testEqualsHashCode() {
        EqualsVerifier.forClass(ProductEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }
}
