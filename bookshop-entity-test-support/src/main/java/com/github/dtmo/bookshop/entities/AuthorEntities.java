package com.github.dtmo.bookshop.entities;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class AuthorEntities {
    public static Supplier<AuthorEntity> createIncrementingAuthorNameSupplier() {
        return new Supplier<AuthorEntity>() {
            private LongSupplier counter = Suppliers.createIncrementingLongSupplier();

            @Override
            public AuthorEntity get() {
                return new AuthorEntity(String.format("Author #%s", counter.getAsLong()));
            }
        };
    }
}
