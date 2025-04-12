package com.github.dtmo.bookshop.generators;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.dtmo.bookshop.entities.BookEntity;

public class BookEntities {
    public static Supplier<BookEntity> createIncrementingTitleNameSupplier() {
        return new Supplier<BookEntity>() {
            private LongSupplier counter = Sequences.createIncrementingLongSupplier();
            
            @Override
            public BookEntity get() {
                return new BookEntity(String.format("Book #%s", counter.getAsLong()));
            }
        };
    }
}
