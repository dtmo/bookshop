package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaymentCardEntities {
    public static Supplier<PaymentCardEntity> createIncrementingPaymentCardSupplier(final AccountEntity accountEntity) {
        return new Supplier<PaymentCardEntity>() {
            private final LongSupplier nameCounter = Suppliers.createIncrementingLongSupplier();
            private final Supplier<String> nameSupplier = () -> String.format("Payment Card #%s",
                    nameCounter.getAsLong());

            private final LongSupplier cardholderNameCounter = Suppliers.createIncrementingLongSupplier();
            private final Supplier<String> cardholderNameSupplier = () -> String.format("Cardholder #%s",
                    cardholderNameCounter.getAsLong());

            private final Supplier<String> cardNumberSupplier = () -> Stream
                    .generate(Suppliers.createRandomDigitSupplier())
                    .limit(16)
                    .map(String::valueOf)
                    .collect(Collectors.joining());

            private final Supplier<LocalDate> expirySupplier = () -> LocalDate.now().plusYears(3);

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
     * need to be fetched separately.
     *
     * @param expected The PaymentCardEntity instance that represents the expected
     *                 values.
     * @param actual   The PaymentCardEntity instance that is to be verified.
     */
    public static void verifyPaymentCardEntity(final PaymentCardEntity expected, final PaymentCardEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actual.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCardholderName(), actual.getCardholderName());
        assertEquals(expected.getCardNumber(), actual.getCardNumber());
        assertEquals(expected.getExpiry(), actual.getExpiry());
    }
}
