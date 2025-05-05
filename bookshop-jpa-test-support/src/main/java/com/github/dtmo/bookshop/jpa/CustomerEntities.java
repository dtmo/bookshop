package com.github.dtmo.bookshop.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class CustomerEntities {
    public static Supplier<CustomerEntity> createIncrementingNameSupplier() {
        return new Supplier<CustomerEntity>() {
            private final LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public CustomerEntity get() {
                return new CustomerEntity(String.format("Customer#%s", counter.getAsLong()));
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a CustomerEntity instance
     * are as expected. This will not attempt to access any fields that may need to
     * be fetched separately.
     * 
     * @param expected The CustomerEntity instance that represents the expected
     *                 values.
     * @param actual   The CustomerEntity instance that is to be verified.
     */
    public static void verifyCustomerEntity(final CustomerEntity expected, final CustomerEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        // The database can't store the full precision of an Instant, so we truncate
        // them for comparisons
        assertEquals(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actual.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected.getUsername(), actual.getUsername());
    }
}
