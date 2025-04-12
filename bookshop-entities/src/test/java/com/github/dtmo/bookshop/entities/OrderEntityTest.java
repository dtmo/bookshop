package com.github.dtmo.bookshop.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.OrderEntity.OrderState;
import com.github.dtmo.bookshop.entities.OrderLineItemEntity.OrderLineItemId;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class OrderEntityTest {
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
                .authors(Set.of(authorEntity))
                .language("en")
                .build();
        final BookEntity blueBookEntity = BookEntity.builder()
                .id(2L)
                .stockKeepingUnit("book2")
                .price(1000L)
                .title("Blue Book")
                .authors(Set.of(authorEntity))
                .language("en")
                .build();
        authorEntity.setBooks(Set.of(redBookEntity, blueBookEntity));
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
        redAccountEntity.setPaymentCardEntities(Set.of(redPaymentCardEntity));
        blueAccountEntity.setPaymentCardEntities(Set.of(bluePaymentCardEntity));
        final OrderEntity redOrderEntity = OrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(redPaymentCardEntity)
                .state(OrderState.WAITING)
                .account(redAccountEntity)
                .build();
        final OrderEntity blueOrderEntity = OrderEntity.builder()
                .id(1L)
                .creationTime(Instant.now())
                .paymentCard(bluePaymentCardEntity)
                .state(OrderState.WAITING)
                .account(blueAccountEntity)
                .build();
        final OrderLineItemEntity redOrderLineItemEntity = OrderLineItemEntity.builder()
                .id(new OrderLineItemId(redOrderEntity, redBookEntity))
                .quantity(1L)
                .unitPrice(1000L)
                .build();
        final OrderLineItemEntity blueOrderLineItemEntity = OrderLineItemEntity.builder()
                .id(new OrderLineItemId(blueOrderEntity, blueBookEntity))
                .quantity(1L)
                .unitPrice(1000L)
                .build();
        EqualsVerifier.forClass(OrderEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(AccountEntity.class, redAccountEntity, blueAccountEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .withPrefabValues(OrderLineItemEntity.class, redOrderLineItemEntity, blueOrderLineItemEntity)
                .verify();

    }
}
