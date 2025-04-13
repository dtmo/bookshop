package com.github.dtmo.bookshop.entities;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class BookEntities {
    public static Supplier<BookEntity> createIncrementingTitleNameSupplier() {
        return new Supplier<BookEntity>() {
            private LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public BookEntity get() {
                return new BookEntity(String.format("Book #%s", counter.getAsLong()));
            }
        };
    }
}
