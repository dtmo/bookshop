package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

public class ShoppingBasketItemEntityIntegrationTest extends AbstractIntegrationTest {
    private EntityManager entityManager;

    @BeforeEach
    public void beforeEach() {
        this.entityManager = AbstractIntegrationTest.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    public void afterEach() {
        entityManager.close();
    }

    @Test
    public void testPersistShoppingBasketItem() {
        // Given and account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a product exists
        final BookEntity bookEntity = getBookEntitySupplier().get();
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        // When a shopping basket item is persisted
        entityManager.getTransaction().begin();
        final ShoppingBasketItemEntity expectedShoppingBasketItemEntity = new ShoppingBasketItemEntity(accountEntity,
                bookEntity, 1);
        entityManager.persist(expectedShoppingBasketItemEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the shopping basket item is stored as expected
        final ShoppingBasketItemEntity actualShoppingBasketItemEntity = entityManager
                .find(ShoppingBasketItemEntity.class, expectedShoppingBasketItemEntity.getId());
        assertNotSame(expectedShoppingBasketItemEntity, actualShoppingBasketItemEntity);
        ShoppingBasketItemEntities.verifyShoppingBasketItemEntity(expectedShoppingBasketItemEntity,
                actualShoppingBasketItemEntity);

        // And the shopping basket item is in the account's shopping basket items
        final AccountEntity actualAccountEntity = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.shoppingBasketItems WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();
        assertTrue(actualAccountEntity.getShoppingBasketItems().contains(expectedShoppingBasketItemEntity));
    }

    @Test
    public void testUpdateShoppingBasketItemQuantity() {
        // Given and account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a product exists
        final BookEntity bookEntity = getBookEntitySupplier().get();
        entityManager.persist(bookEntity);

        // And a shopping basket item is added to the account
        final ShoppingBasketItemEntity shoppingBasketItemEntity = new ShoppingBasketItemEntity(accountEntity,
                bookEntity, 1);
        entityManager.persist(shoppingBasketItemEntity);
        entityManager.getTransaction().commit();

        // When the shopping basket item quantity is updated
        final long expectedQuantity = shoppingBasketItemEntity.getQuantity() + 3;
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE ShoppingBasketItemEntity s SET s.quantity = :quantity")
                .setParameter("quantity", expectedQuantity)
                .executeUpdate();
        entityManager.getTransaction().commit();

        // Then the shopping basket item quantity is updated as expected
        entityManager.refresh(shoppingBasketItemEntity);
        assertEquals(expectedQuantity, shoppingBasketItemEntity.getQuantity());
    }

    @Test
    public void testDeletePaymentCard() {
        // Given and account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a product exists
        final BookEntity bookEntity = getBookEntitySupplier().get();
        entityManager.persist(bookEntity);

        // And a shopping basket item is added to the account
        final ShoppingBasketItemEntity shoppingBasketItemEntity = new ShoppingBasketItemEntity(accountEntity,
                bookEntity, 1);
        entityManager.persist(shoppingBasketItemEntity);
        entityManager.getTransaction().commit();

        // When the shopping basked item is deleted
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE ShoppingBasketItemEntity a WHERE a = :shoppingBasketItem")
                .setParameter("shoppingBasketItem", shoppingBasketItemEntity)
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the payment card no longer exists
        assertNull(entityManager.find(ShoppingBasketItemEntity.class, shoppingBasketItemEntity.getId()));

        // And the shopping basket item is no longer in the accounts shopping basket
        // items
        final AccountEntity actualAccountEntity = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.shoppingBasketItems WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();
        assertFalse(actualAccountEntity.getShoppingBasketItems().contains(shoppingBasketItemEntity));
    }
}
