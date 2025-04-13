package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class SuppliersTest {
    @Test
    void testCreateIncrementingLongSupplier() {
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L),
                LongStream.generate(Suppliers.createIncrementingLongSupplier())
                        .limit(10)
                        .mapToObj(Long::valueOf)
                        .collect(Collectors.toList()));
    }

    @Test
    void testCreateIncrementingLongSupplierWithFrom() {
        assertEquals(List.of(2L, 3L, 4L, 5L, 6L),
                LongStream.generate(Suppliers.createIncrementingLongSupplier(2))
                        .limit(5)
                        .mapToObj(Long::valueOf)
                        .collect(Collectors.toList()));
    }

    @Test
    void testCreateIncrementingLongSupplierWithFromAndIncrement() {
        assertEquals(List.of(2L, 5L, 8L, 11L, 14L),
                LongStream.generate(Suppliers.createIncrementingLongSupplier(2, 3))
                        .limit(5)
                        .mapToObj(Long::valueOf)
                        .collect(Collectors.toList()));
    }

    @Test
    void testCreateRandomDigitSupplier() {
        assertTrue(Pattern.matches("\\d{16}", Stream.generate(Suppliers.createRandomDigitSupplier())
                .limit(16)
                .map(String::valueOf)
                .collect(Collectors.joining())));
    }
}
