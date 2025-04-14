package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;

public class ProductEntities {
    /**
     * Asserts that all the fields a ProductEntity instance are as expected.
     * 
     * @param expected The ProductEntity instance that represents the expected
     *                 values.
     * @param actual   The ProductEntity instance that is to be verified.
     */
    public static void verifyProductEntity(final ProductEntity expected, final ProductEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        // The database can't store the full precision of an Instant, se we truncate
        // them for comparisons
        assertEquals(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actual.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected.getStockKeepingUnit(), actual.getStockKeepingUnit());
        assertEquals(expected.getPrice(), actual.getPrice());
    }
}
