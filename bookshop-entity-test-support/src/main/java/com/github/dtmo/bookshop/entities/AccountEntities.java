package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class AccountEntities {
    public static Supplier<AccountEntity> createIncrementingNameSupplier() {
        return new Supplier<AccountEntity>() {
            private LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public AccountEntity get() {
                return new AccountEntity(String.format("Account #%s", counter.getAsLong()));
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a AccountEntity instance
     * are as expected. This will not attempt to access any fields that may beed to
     * be fetched separately.
     *
     * @param expectedAccountEntity The AccountEntity instance that represents the
     *                              expected values.
     * @param actualAccountEntity   The AccountEntity instance that is to be
     *                              verified.
     */
    public static void verifyAccountEntity(final AccountEntity expectedAccountEntity,
            final AccountEntity actualAccountEntity) {
        assertEquals(expectedAccountEntity.getId(), actualAccountEntity.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expectedAccountEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actualAccountEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expectedAccountEntity.getName(), actualAccountEntity.getName());
    }
}
