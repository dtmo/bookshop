package com.github.dtmo.bookshop.entities;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class AccountEntityTest {
    @Test
    public void testEqualsHashcode() {
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

        EqualsVerifier.forClass(AccountEntity.class)
                .suppress(Warning.SURROGATE_KEY)
                .withPrefabValues(CustomerEntity.class, redCustomerEntity, blueCustomerEntity)
                .withPrefabValues(PaymentCardEntity.class, redPaymentCardEntity, bluePaymentCardEntity)
                .verify();
    }
}
