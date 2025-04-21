package com.github.dtmo.bookshop.es;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.opensearch.BookDocument;

import nl.jqno.equalsverifier.EqualsVerifier;

public class BookDocumentTest {
    @Test
    void testEqualsHashcode() {
        EqualsVerifier.forClass(BookDocument.class)
                .verify();
    }
}
