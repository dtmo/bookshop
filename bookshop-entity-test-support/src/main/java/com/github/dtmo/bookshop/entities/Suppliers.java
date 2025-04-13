package com.github.dtmo.bookshop.entities;

import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class Suppliers {
    public static LongSupplier createIncrementingLongSupplier(final long from, final int increment) {
        return new LongSupplier() {
            private long counter = from;

            @Override
            public long getAsLong() {
                long value = counter;
                counter += increment;
                return value;
            }
        };
    }

    public static LongSupplier createIncrementingLongSupplier(final long from) {
        return createIncrementingLongSupplier(from, 1);
    }

    public static LongSupplier createIncrementingLongSupplier() {
        return createIncrementingLongSupplier(0);
    }

    public static Supplier<Character> createRandomDigitSupplier() {
        return () -> Character.valueOf((char) ('0' + (Math.random() * 10)));
    }
}
