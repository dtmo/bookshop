package com.github.dtmo.bookshop.entities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.InProgressOrderEntity.InProgressOrderState;

import nl.jqno.equalsverifier.EqualsVerifier;

public class InProgressOrderLineItemIdTest {
    @Test
    void testEqualsHashCode() {
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
                .state(InProgressOrderState.DISPATCHED)
                .account(accountEntity)
                .build();
        final InProgressOrderEntity blueInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(2L)
                .creationTime(Instant.now().plus(Duration.ofSeconds(2)))
                .paymentCard(paymentCardEntity)
                .state(InProgressOrderState.WAITING)
                .account(accountEntity)
                .build();
        EqualsVerifier.forClass(InProgressOrderLineItemEntity.InProgressOrderLineItemId.class)
                .withPrefabValues(InProgressOrderEntity.class, redInProgressOrderEntity, blueInProgressOrderEntity)
                .verify();
    }
}
