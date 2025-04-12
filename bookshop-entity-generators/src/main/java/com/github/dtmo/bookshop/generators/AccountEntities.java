package com.github.dtmo.bookshop.generators;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.dtmo.bookshop.entities.AccountEntity;

public class AccountEntities {
    public static Supplier<AccountEntity> createIncrementingNameSupplier() {
        return new Supplier<AccountEntity>() {
            private LongSupplier counter = Sequences.createIncrementingLongSupplier();

            @Override
            public AccountEntity get() {
                return AccountEntity.builder()
                        .name(String.format("Account #%s", counter.getAsLong()))
                        .build();
            }
        };
    }
}
