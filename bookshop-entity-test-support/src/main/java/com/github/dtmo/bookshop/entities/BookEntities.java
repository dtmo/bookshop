package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class BookEntities {
    public static Supplier<BookEntity> createIncrementingBookSupplier() {
        return new Supplier<BookEntity>() {
            final LongSupplier priceSupplier = () -> (long) (1000 + (Math.random() * 1000));

            private final LongSupplier stockKeepingUnitCounter = Suppliers.createIncrementingLongSupplier();
            private final Supplier<String> stockKeepingUnitSupplier = () -> String.format("SKUBOOK#%s",
                    stockKeepingUnitCounter.getAsLong());

            private final LongSupplier titleCounter = Suppliers.createIncrementingLongSupplier();
            private final Supplier<String> titleSupplier = () -> String.format("Book #%s", titleCounter.getAsLong());

            @Override
            public BookEntity get() {
                return new BookEntity(priceSupplier.getAsLong(), stockKeepingUnitSupplier.get(), titleSupplier.get());
            }
        };
    }

    /**
     * Asserts that all the local (non-reference) fields a BookEntity instance
     * are as expected. This will not attempt to access any fields that may need to
     * be fetched separately.
     * 
     * @param expected The BookEntity instance that represents the expected values.
     * @param actual   The BookEntity instance that is to be verified.
     */
    public static void verifyBookEntity(final BookEntity expected, final BookEntity actual) {
        ProductEntities.verifyProductEntity(expected, actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getProductionCredits(), actual.getProductionCredits());
        assertEquals(expected.getSummary(), actual.getSummary());
        assertEquals(expected.getLanguage(), actual.getLanguage());
        assertEquals(expected.getSubject(), actual.getSubject());
    }
}
