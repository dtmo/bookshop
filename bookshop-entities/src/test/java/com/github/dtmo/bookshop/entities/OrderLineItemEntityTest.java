package com.github.dtmo.bookshop.entities;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.OrderEntity.OrderState;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class OrderLineItemEntityTest {
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
                .build();
        final OrderEntity redOrderEntity = OrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(paymentCardEntity)
                .state(OrderState.DISPATCHED)
                .account(accountEntity)
                .build();
        final OrderEntity blueOrderEntity = OrderEntity.builder()
                .id(2L)
                .creationTime(Instant.now().plus(Duration.ofSeconds(2)))
                .paymentCard(paymentCardEntity)
                .state(OrderState.WAITING)
                .account(accountEntity)
                .build();
        EqualsVerifier.forClass(OrderLineItemEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(OrderEntity.class, redOrderEntity, blueOrderEntity)
                .verify();
    }
}
