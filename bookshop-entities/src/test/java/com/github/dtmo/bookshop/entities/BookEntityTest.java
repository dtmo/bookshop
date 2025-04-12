package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class BookEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(BookEntity.class)
                // BookEntity relies on the equals and hashCode methods implemented on
                // its parent class: ProductEntity
                // We suppress the EqualsVerified ALL_FIELDS_SHOULD_BE_USED to indicate
                // that we're deliberately ignoring BookEntity fields.
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .withPrefabValues(AuthorEntity.class,
                        AuthorEntity.builder().id(1L).name("Red Author").build(),
                        AuthorEntity.builder().id(2L).name("Blue Author").build())
                .verify();
    }
}
