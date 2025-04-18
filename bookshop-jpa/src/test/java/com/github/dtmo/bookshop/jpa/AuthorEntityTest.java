package com.github.dtmo.bookshop.jpa;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AuthorEntityTest {
    @Test
    public void testEqualsHashcode() {
        final AuthorEntity authorEntity = AuthorEntity.builder()
                .id(1L)
                .name("Test Author")
                .build();

        final BookEntity redBookEntity = BookEntity.builder()
                .id(1L)
                .stockKeepingUnit("book1")
                .price(1000L)
                .title("Red Book")
                .language(Locale.ENGLISH.getLanguage())
                .build();
        redBookEntity.getAuthors().add(authorEntity);
        authorEntity.getBooks().add(redBookEntity);

        final BookEntity blueBookEntity = BookEntity.builder()
                .id(2L)
                .stockKeepingUnit("book2")
                .price(1000L)
                .title("Blue Book")
                .language(Locale.ENGLISH.getLanguage())
                .build();
        blueBookEntity.getAuthors().add(authorEntity);
        authorEntity.getBooks().add(blueBookEntity);

        EqualsVerifier.forClass(AuthorEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(BookEntity.class, redBookEntity, blueBookEntity)
                .verify();
    }
}
