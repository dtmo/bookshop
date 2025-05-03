package com.github.dtmo.bookshop.jpa;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.jpa.InProgressOrderEntity.InProgressOrderState;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class InProgressOrderLineItemEntityTest {
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
                .build();

        final BookEntity blueBookEntity = BookEntity.builder()
                .id(2L)
                .stockKeepingUnit("book2")
                .price(1000L)
                .build();
        blueBookEntity.getAuthors().add(authorEntity);
        authorEntity.getBooks().add(blueBookEntity);

        redBookEntity.getAuthors().add(authorEntity);
        authorEntity.getBooks().add(redBookEntity);

        final AccountEntity accountEntity = AccountEntity.builder()
                .id(1L)
                .name("Test Account")
                .build();

        final PaymentCardEntity paymentCardEntity = PaymentCardEntity.builder()
                .id(1L)
                .name("test card")
                .cardholderName("Test Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(accountEntity)
                .build();

        final InProgressOrderEntity redInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(paymentCardEntity)
                .state(InProgressOrderState.PROCESSING)
                .account(accountEntity)
                .build();

        final InProgressOrderEntity blueInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(2L)
                .creationTime(Instant.now().plus(Duration.ofSeconds(2)))
                .paymentCard(paymentCardEntity)
                .state(InProgressOrderState.WAITING)
                .account(accountEntity)
                .build();
        EqualsVerifier.forClass(InProgressOrderLineItemEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(BookEntity.class, redBookEntity, blueBookEntity)
                .withPrefabValues(InProgressOrderEntity.class, redInProgressOrderEntity, blueInProgressOrderEntity)
                .verify();
    }
}
