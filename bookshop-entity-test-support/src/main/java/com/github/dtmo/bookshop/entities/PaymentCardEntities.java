package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaymentCardEntities {
    public static Supplier<PaymentCardEntity> createInrcementingPaymentCardSupplier(final AccountEntity accountEntity) {
        return new Supplier<PaymentCardEntity>() {
            final LongSupplier nameCounter = Suppliers.createIncrementingLongSupplier();
            final Supplier<String> nameSupplier = () -> String.format("Payment Card #%s", nameCounter.getAsLong());

            final LongSupplier cardholderNameCounter = Suppliers.createIncrementingLongSupplier();
            final Supplier<String> cardholderNameSupplier = () -> String.format("Cardholder #%s",
                    cardholderNameCounter.getAsLong());

            final Supplier<String> cardNumberSupplier = () -> Stream.generate(Suppliers.createRandomDigitSupplier())
                    .limit(16)
                    .map(String::valueOf)
                    .collect(Collectors.joining());

            final Supplier<LocalDate> expirySupplier = () -> LocalDate.now().plusYears(3);

            @Override
            public PaymentCardEntity get() {
                return new PaymentCardEntity(nameSupplier.get(),
                        cardholderNameSupplier.get(), cardNumberSupplier.get(),
                        expirySupplier.get(), accountEntity);
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a PaymentCardEntity
     * instance are as expected. This will not attempt to access any fields that may
     * beed to be fetched separately.
     *
     * @param expectedPaymentCardEntity The PaymentCardEntity instance that
     *                                  represents the expected values.
     * @param actualPaymentCardEntity   The PaymentCardEntity instance that is to be
     *                                  verified.
     */
    public static void verifyPaymentCardEntity(final PaymentCardEntity expectedPaymentCardEntity,
            final PaymentCardEntity actualPaymentCardEntity) {
        assertEquals(expectedPaymentCardEntity.getId(), actualPaymentCardEntity.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expectedPaymentCardEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actualPaymentCardEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expectedPaymentCardEntity.getName(), actualPaymentCardEntity.getName());
        assertEquals(expectedPaymentCardEntity.getCardholderName(), actualPaymentCardEntity.getCardholderName());
        assertEquals(expectedPaymentCardEntity.getCardNumber(), actualPaymentCardEntity.getCardNumber());
        assertEquals(expectedPaymentCardEntity.getExpiry(), actualPaymentCardEntity.getExpiry());
    }
}
