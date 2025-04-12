package com.github.dtmo.bookshop.generators;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.dtmo.bookshop.entities.AuthorEntity;

public class AuthorEntities {
    public static Supplier<AuthorEntity> createIncrementingAuthorNameSupplier() {
        return new Supplier<AuthorEntity>() {
            private LongSupplier counter = Sequences.createIncrementingLongSupplier();
            
            @Override
            public AuthorEntity get() {
                return new AuthorEntity(String.format("Author #%s", counter.getAsLong()));
            }
        };
    }
}
