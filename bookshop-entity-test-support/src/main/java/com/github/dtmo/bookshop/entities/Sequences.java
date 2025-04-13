package com.github.dtmo.bookshop.entities;

import java.util.function.LongSupplier;

public class Sequences {
    public static LongSupplier createIncrementingLongSupplier(final long from, final int increment) {
        return new LongSupplier() {
            private long counter = from;

            @Override
            public long getAsLong() {
                return counter += increment;
            }
        };
    }

    public static LongSupplier createIncrementingLongSupplier(final long from) {
        return createIncrementingLongSupplier(from, 1);
    }

    public static LongSupplier createIncrementingLongSupplier() {
        return createIncrementingLongSupplier(0);
    }
}
