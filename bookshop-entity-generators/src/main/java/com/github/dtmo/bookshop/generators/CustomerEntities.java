package com.github.dtmo.bookshop.generators;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.dtmo.bookshop.entities.CustomerEntity;

public class CustomerEntities {
    public static Supplier<CustomerEntity> createIncrementingNameSupplier() {
        return new Supplier<CustomerEntity>() {
            private LongSupplier counter = Sequences.createIncrementingLongSupplier();

            @Override
            public CustomerEntity get() {
                return CustomerEntity.builder()
                        .name(String.format("Customer #%s", counter.getAsLong()))
                        .build();
            }
        };
    }

}
