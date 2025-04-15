package com.github.dtmo.bookshop.entities;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.InProgressOrderEntity.InProgressOrderState;
import com.github.dtmo.bookshop.entities.InProgressOrderLineItemEntity.InProgressOrderLineItemId;
import com.github.dtmo.bookshop.entities.ShoppingBasketItemEntity.ShoppingBasketItemId;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AccountEntityTest {
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

        final CustomerEntity redCustomerEntity = CustomerEntity.builder()
                .id(1L)
                .name("Red Customer")
                .build();

        final AccountEntity redAccountEntity = AccountEntity.builder()
                .id(1L)
                .name("Red Account")
                .build();
        redAccountEntity.getCustomers().add(redCustomerEntity);
        redCustomerEntity.getAccounts().add(redAccountEntity);

        final PaymentCardEntity redPaymentCardEntity = PaymentCardEntity.builder()
                .id(1L)
                .name("Red Payment Card")
                .cardholderName("Red Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(redAccountEntity)
                .build();
        redAccountEntity.getPaymentCards().add(redPaymentCardEntity);

        final ShoppingBasketItemEntity redShoppingBasketItemEntity = ShoppingBasketItemEntity.builder()
                .id(new ShoppingBasketItemId(redAccountEntity, redBookEntity))
                .quantity(1L)
                .build();
        redAccountEntity.getShoppingBasketItems().add(redShoppingBasketItemEntity);

        final InProgressOrderEntity redInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(1L)
                .paymentCard(redPaymentCardEntity)
                .account(redAccountEntity)
                .state(InProgressOrderState.WAITING)
                .build();
        redInProgressOrderEntity.getLineItems()
                .add(InProgressOrderLineItemEntity.builder()
                        .id(new InProgressOrderLineItemId(redInProgressOrderEntity, redBookEntity))
                        .unitPrice(redBookEntity.getPrice())
                        .quantity(1)
                        .build());

        final CustomerEntity blueCustomerEntity = CustomerEntity.builder()
                .id(2L)
                .name("Blue Customer")
                .build();

        final AccountEntity blueAccountEntity = AccountEntity.builder()
                .id(2L)
                .name("Blue Account")
                .build();
        blueAccountEntity.getCustomers().add(blueCustomerEntity);
        blueCustomerEntity.getAccounts().add(redAccountEntity);

        final PaymentCardEntity bluePaymentCardEntity = PaymentCardEntity.builder()
                .id(2L)
                .name("Blue Payment Card")
                .cardholderName("Blue Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(blueAccountEntity)
                .build();
        blueAccountEntity.getPaymentCards().add(bluePaymentCardEntity);

        final ShoppingBasketItemEntity blueShoppingBasketItemEntity = ShoppingBasketItemEntity.builder()
                .id(new ShoppingBasketItemId(blueAccountEntity, blueBookEntity))
                .quantity(1L)
                .build();
        blueAccountEntity.getShoppingBasketItems().add(blueShoppingBasketItemEntity);

        final InProgressOrderEntity blueInProgressOrderEntity = InProgressOrderEntity.builder()
                .id(2L)
                .paymentCard(bluePaymentCardEntity)
                .account(blueAccountEntity)
                .state(InProgressOrderState.WAITING)
                .build();
        blueInProgressOrderEntity.getLineItems()
                .add(InProgressOrderLineItemEntity.builder()
                        .id(new InProgressOrderLineItemId(blueInProgressOrderEntity, blueBookEntity))
                        .unitPrice(blueBookEntity.getPrice())
                        .quantity(1)
                        .build());

        EqualsVerifier.forClass(AccountEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(CustomerEntity.class, redCustomerEntity, blueCustomerEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .withPrefabValues(ShoppingBasketItemEntity.class, redShoppingBasketItemEntity,
                        blueShoppingBasketItemEntity)
                .withPrefabValues(InProgressOrderEntity.class, redInProgressOrderEntity, blueInProgressOrderEntity)
                .verify();
    }
}
