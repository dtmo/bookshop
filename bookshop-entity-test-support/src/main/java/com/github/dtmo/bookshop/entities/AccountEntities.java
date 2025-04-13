package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class AccountEntities {
    public static Supplier<AccountEntity> createIncrementingNameSupplier() {
        return new Supplier<AccountEntity>() {
            private LongSupplier counter = Sequences.createIncrementingLongSupplier();

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
        assertEquals(expectedAccountEntity.getCreationTime(), actualAccountEntity.getCreationTime());
        assertEquals(expectedAccountEntity.getName(), actualAccountEntity.getName());
    }
}
