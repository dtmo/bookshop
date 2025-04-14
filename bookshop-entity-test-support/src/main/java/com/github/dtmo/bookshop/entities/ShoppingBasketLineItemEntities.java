package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingBasketLineItemEntities {
    /**
     * Asserts that all the fields a ShoppingBasketLineItemEntity instance are as
     * expected.
     * 
     * @param expected The ShoppingBasketLineItemEntity instance that represents the
     *                 expected values.
     * @param actual   The ShoppingBasketLineItemEntity instance that is to be
     *                 verified.
     */
    public static void verifyShoppingBasketLineItemEntity(final ShoppingBasketLineItemEntity expected,
            final ShoppingBasketLineItemEntity actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getQuantity(), actual.getQuantity());

        // We assertEquals here rather than calling the verifiers so that we only
        // compare the IDs
        assertEquals(expected.getAccount(), actual.getAccount());
        assertEquals(expected.getProduct(), actual.getProduct());
    }
}
