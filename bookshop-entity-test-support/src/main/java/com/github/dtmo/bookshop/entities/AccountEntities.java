package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class AccountEntities {
    public static Supplier<AccountEntity> createIncrementingNameSupplier() {
        return new Supplier<AccountEntity>() {
            private final LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public AccountEntity get() {
                return new AccountEntity(String.format("Account #%s", counter.getAsLong()));
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a AccountEntity instance
     * are as expected. This will not attempt to access any fields that may need to
     * be fetched separately.
     *
     * @param expected The AccountEntity instance that represents the expected
     *                 values.
     * @param actual   The AccountEntity instance that is to be verified.
     */
    public static void verifyAccountEntity(final AccountEntity expected, final AccountEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actual.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected.getName(), actual.getName());
    }
}
