package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class CustomerEntities {
    public static Supplier<CustomerEntity> createIncrementingNameSupplier() {
        return new Supplier<CustomerEntity>() {
            private LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public CustomerEntity get() {
                return new CustomerEntity(String.format("Customer #%s", counter.getAsLong()));
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a CustomerEntity instance
     * are as expected. This will not attempt to access any fields that may beed to
     * be fetched separately.
     * 
     * @param expectedCustomerEntity The CustomerEntity instance that represents the
     *                               expected values.
     * @param actualCustomerEntity   The CustomerEntity instance that is to be
     *                               verified.
     */
    public static void verifyCustomerEntity(final CustomerEntity expectedCustomerEntity,
            final CustomerEntity actualCustomerEntity) {
        assertEquals(expectedCustomerEntity.getId(), actualCustomerEntity.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expectedCustomerEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actualCustomerEntity.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expectedCustomerEntity.getName(), actualCustomerEntity.getName());
    }
}
