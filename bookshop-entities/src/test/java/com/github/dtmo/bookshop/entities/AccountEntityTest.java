package com.github.dtmo.bookshop.entities;

import java.time.LocalDate;
import java.time.Month;

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
        redAccountEntity.getPaymentCardEntities().add(redPaymentCardEntity);

        final ShoppingBasketLineItemEntity redShoppingBasketLineItemEntity = ShoppingBasketLineItemEntity.builder()
                .id(new ShoppingBasketLineItemId(redAccountEntity, redBookEntity))
                .quantity(1L)
                .build();
        redAccountEntity.getShopppingBasketLineItems().add(redShoppingBasketLineItemEntity);

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
        blueAccountEntity.getPaymentCardEntities().add(bluePaymentCardEntity);

        final ShoppingBasketLineItemEntity blueShoppingBasketLineItemEntity = ShoppingBasketLineItemEntity.builder()
                .id(new ShoppingBasketLineItemId(blueAccountEntity, blueBookEntity))
                .quantity(1L)
                .build();
        blueAccountEntity.getShopppingBasketLineItems().add(blueShoppingBasketLineItemEntity);

        EqualsVerifier.forClass(AccountEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(CustomerEntity.class, redCustomerEntity, blueCustomerEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .withPrefabValues(ShoppingBasketLineItemEntity.class, redShoppingBasketLineItemEntity,
                        blueShoppingBasketLineItemEntity)
                .verify();
    }
}
