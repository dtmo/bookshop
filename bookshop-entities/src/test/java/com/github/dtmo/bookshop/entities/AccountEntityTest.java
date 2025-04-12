package com.github.dtmo.bookshop.entities;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.ShoppingBasketLineItemEntity.ShoppingBasketLineItemId;

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
        final CustomerEntity redCustomerEntity = CustomerEntity.builder()
                .id(1L)
                .name("Red Customer")
                .build();
        final AccountEntity redAccountEntity = AccountEntity.builder()
                .id(1L)
                .name("Red Account")
                .customers(Set.of(redCustomerEntity))
                .build();
        redCustomerEntity.setAccounts(Set.of(redAccountEntity));
        final PaymentCardEntity redPaymentCardEntity = PaymentCardEntity.builder()
                .id(1L)
                .name("Red Payment Card")
                .cardholderName("Red Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(redAccountEntity)
                .build();
        redAccountEntity.setPaymentCardEntities(Set.of(redPaymentCardEntity));
        final ShoppingBasketLineItemEntity redShoppingBasketLineItemEntity = ShoppingBasketLineItemEntity.builder()
                .id(new ShoppingBasketLineItemId(redAccountEntity, redBookEntity))
                .quantity(1L)
                .build();
        redAccountEntity.setShopppingBasketLineItems(List.of(redShoppingBasketLineItemEntity));

        final CustomerEntity blueCustomerEntity = CustomerEntity.builder()
                .id(2L)
                .name("Blue Customer")
                .build();
        final AccountEntity blueAccountEntity = AccountEntity.builder()
                .id(2L)
                .name("Blue Account")
                .customers(Set.of(blueCustomerEntity))
                .build();
        blueCustomerEntity.setAccounts(Set.of(redAccountEntity));
        final PaymentCardEntity bluePaymentCardEntity = PaymentCardEntity.builder()
                .id(2L)
                .name("Blue Payment Card")
                .cardholderName("Blue Cardholder")
                .cardNumber("1234567812345678")
                .expiry(LocalDate.of(2525, Month.JANUARY, 2))
                .account(blueAccountEntity)
                .build();
        blueAccountEntity.setPaymentCardEntities(Set.of(bluePaymentCardEntity));
        final ShoppingBasketLineItemEntity blueShoppingBasketLineItemEntity = ShoppingBasketLineItemEntity.builder()
                .id(new ShoppingBasketLineItemId(blueAccountEntity, blueBookEntity))
                .quantity(1L)
                .build();
        blueAccountEntity.setShopppingBasketLineItems(List.of(blueShoppingBasketLineItemEntity));

        EqualsVerifier.forClass(AccountEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(CustomerEntity.class, redCustomerEntity, blueCustomerEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .withPrefabValues(ShoppingBasketLineItemEntity.class, redShoppingBasketLineItemEntity,
                        blueShoppingBasketLineItemEntity)
                .verify();
    }
}
