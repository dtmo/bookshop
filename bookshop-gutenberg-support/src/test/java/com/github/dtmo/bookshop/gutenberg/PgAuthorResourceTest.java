package com.github.dtmo.bookshop.gutenberg;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PgAuthorResourceTest {
    @Test
    void testEqualsHashcode() {
        EqualsVerifier.forClass(PgAuthorResource.class).verify();
    }
}
