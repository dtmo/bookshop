package com.github.dtmo.bookshop.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.InProgressOrderEntity.InProgressOrderState;
import com.github.dtmo.bookshop.entities.InProgressOrderLineItemEntity.InProgressOrderLineItemId;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class InProgressOrderEntityTest {
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
                .language("en")
                .build();
        redBookEntity.getAuthors().add(authorEntity);
        authorEntity.getBooks().add(redBookEntity);

        final BookEntity blueBookEntity = BookEntity.builder()
                .id(2L)
                .stockKeepingUnit("book2")
                .price(1000L)
                .title("Blue Book")
                .language("en")
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

        final PaymentCardEntity redPaymentCardEntity = PaymentCardEntity.builder()
                .id(1L)
                .name("Red Payment Card")
                .cardholderName("Red Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(redAccountEntity)
                .build();

        final PaymentCardEntity bluePaymentCardEntity = PaymentCardEntity.builder()
                .id(2L)
                .name("Blue Payment Card")
                .cardholderName("Blue Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(blueAccountEntity)
                .build();

        redAccountEntity.getPaymentCards().add(redPaymentCardEntity);
        blueAccountEntity.getPaymentCards().add(bluePaymentCardEntity);

        final InProgressOrderEntity redInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(redPaymentCardEntity)
                .state(InProgressOrderState.WAITING)
                .account(redAccountEntity)
                .build();

        final InProgressOrderEntity blueInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(bluePaymentCardEntity)
                .state(InProgressOrderState.WAITING)
                .account(blueAccountEntity)
                .build();

        final InProgressOrderLineItemEntity redInProgressOrderLineItemEntity = InProgressOrderLineItemEntity.builder()
                .id(new InProgressOrderLineItemId(redInProgressOrderEntity, redBookEntity))
                .quantity(1L)
                .unitPrice(1000L)
                .build();

        final InProgressOrderLineItemEntity blueInProgressOrderLineItemEntity = InProgressOrderLineItemEntity.builder()
                .id(new InProgressOrderLineItemId(blueInProgressOrderEntity, blueBookEntity))
                .quantity(1L)
                .unitPrice(1000L)
                .build();

        EqualsVerifier.forClass(InProgressOrderEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(AccountEntity.class, redAccountEntity, blueAccountEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .withPrefabValues(InProgressOrderLineItemEntity.class, redInProgressOrderLineItemEntity,
                        blueInProgressOrderLineItemEntity)
                .verify();
    }
}
