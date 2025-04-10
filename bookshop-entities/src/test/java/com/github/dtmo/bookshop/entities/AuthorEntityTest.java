package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AuthorEntityTest {
    @Test
    public void testEqualsHashcode() {
        EqualsVerifier.forClass(AuthorEntity.class).suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(BookEntity.class,
                        BookEntity.builder().id(1L).title("Red Book").build(),
                        BookEntity.builder().id(2L).title("Blue Book").build())
                .verify();
    }
}
