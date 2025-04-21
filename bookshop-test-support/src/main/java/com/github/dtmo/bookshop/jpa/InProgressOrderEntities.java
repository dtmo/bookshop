package com.github.dtmo.bookshop.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.temporal.ChronoUnit;

public class InProgressOrderEntities {
    /**
     * Asserts that all the local (non-reference) fields a InProgressOrderEntity
     * instance are as expected. This will not attempt to access any fields that may
     * need to be fetched separately.
     *
     * @param expected The InProgressOrderEntity instance that represents the
     *                 expected
     *                 values.
     * @param actual   The InProgressOrderEntity instance that is to be verified.
     */
    public static void verifyInProgressOrderEntity(final InProgressOrderEntity expected,
            final InProgressOrderEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        // The database can't store the full precision of an Instant, so we truncate
        // them for comparisons
        assertEquals(expected.getCreationTime().truncatedTo(ChronoUnit.MILLIS),
                actual.getCreationTime().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(expected.getPaymentCard(), actual.getPaymentCard());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getAccount(), actual.getAccount());
    }
}
