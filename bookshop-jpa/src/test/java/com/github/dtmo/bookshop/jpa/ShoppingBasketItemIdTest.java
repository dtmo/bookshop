package com.github.dtmo.bookshop.jpa;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ShoppingBasketItemIdTest {
    @Test
    void testEqualsHashCode() {
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

        final AccountEntity redAccountEntity = AccountEntity.builder()
                .id(1L)
                .name("Red Account")
                .build();
        final AccountEntity blueAccountEntity = AccountEntity.builder()
                .id(2L)
                .name("Blue Account")
                .build();
        EqualsVerifier.forClass(ShoppingBasketItemEntity.ShoppingBasketItemId.class)
                .withPrefabValues(BookEntity.class, redBookEntity, blueBookEntity)
                .withPrefabValues(AccountEntity.class, redAccountEntity, blueAccountEntity)
                .verify();
    }
}
