package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingBasketItemEntities {
    /**
     * Asserts that all the fields a ShoppingBasketItemEntity instance are as
     * expected.
     * 
     * @param expected The ShoppingBasketItemEntity instance that represents the
     *                 expected values.
     * @param actual   The ShoppingBasketItemEntity instance that is to be
     *                 verified.
     */
    public static void verifyShoppingBasketItemEntity(final ShoppingBasketItemEntity expected,
            final ShoppingBasketItemEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getQuantity(), actual.getQuantity());

        // We assertEquals here rather than calling the verifiers so that we only
        // compare the IDs
        assertEquals(expected.getAccount(), actual.getAccount());
        assertEquals(expected.getBook(), actual.getBook());
    }
}
