package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.dtmo.bookshop.entities.InProgressOrderEntity.InProgressOrderState;

import jakarta.persistence.EntityManager;

public class InProgressOrderEntityIntegrationTest extends AbstractIntegrationTest {
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
    public void testPersistInProgressOrder() {
        // Given an account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a payment card is associated with the account
        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);

        // And a product exists
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);
        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        // When an order for the product is persisted
        entityManager.getTransaction().begin();
        final InProgressOrderEntity expectedInProgressOrderEntity = new InProgressOrderEntity(paymentCardEntity,
                InProgressOrderState.WAITING,
                accountEntity);
        entityManager.persist(expectedInProgressOrderEntity);
        final InProgressOrderLineItemEntity inProgressOrderLineItemEntity = new InProgressOrderLineItemEntity(
                expectedInProgressOrderEntity, bookEntity, bookEntity.getPrice(), 1);
        entityManager.persist(inProgressOrderLineItemEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the in-progress order details are present in the database as expected
        final InProgressOrderEntity actualInProgressOrderEntity = entityManager
                .createQuery("FROM InProgressOrderEntity o LEFT JOIN FETCH o.lineItems WHERE o = :order",
                        InProgressOrderEntity.class)
                .setParameter("order", expectedInProgressOrderEntity)
                .getSingleResult();

        assertNotSame(expectedInProgressOrderEntity, actualInProgressOrderEntity);
        InProgressOrderEntities.verifyInProgressOrderEntity(expectedInProgressOrderEntity, actualInProgressOrderEntity);

        assertTrue(actualInProgressOrderEntity.getLineItems().contains(inProgressOrderLineItemEntity));

        // And the in-progress order is present in the account's in-progress orders
        final AccountEntity actualAccountEntity = entityManager
                .createQuery(
                        "FROM AccountEntity a LEFT JOIN FETCH a.inProgressOrders WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();
        assertTrue(actualAccountEntity.getInProgressOrders().contains(actualInProgressOrderEntity));
    }

    @Test
    public void testUpdateInProgressOrderState() {
        // Given an account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a payment card is associated with the account
        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);

        // And an in-progress order exists
        final InProgressOrderEntity inProgressOrderEntity = new InProgressOrderEntity(paymentCardEntity,
                InProgressOrderState.WAITING,
                accountEntity);
        entityManager.persist(inProgressOrderEntity);
        entityManager.getTransaction().commit();

        // When the in-progress order's state is updated
        entityManager.getTransaction().begin();
        final InProgressOrderState expectedInProgressOrderState = InProgressOrderEntity.InProgressOrderState.PROCESSING;
        entityManager.createQuery("UPDATE InProgressOrderEntity o SET o.state = :state WHERE o = :inProgressOrder")
                .setParameter("inProgressOrder", inProgressOrderEntity)
                .setParameter("state", expectedInProgressOrderState)
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the in-progress order's updated state is present in the database
        final InProgressOrderEntity persistedinProgressOrderEntity = entityManager.find(InProgressOrderEntity.class,
                inProgressOrderEntity.getId());
        assertEquals(expectedInProgressOrderState, persistedinProgressOrderEntity.getState());
    }

    @Test
    public void testDeleteInProgressOrder() {
        // Given an account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And a payment card is associated with the account
        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);

        // And a product exists
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);
        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(bookEntity);

        // And an in-progress order for the product exists
        final InProgressOrderEntity inProgressOrderEntity = new InProgressOrderEntity(paymentCardEntity,
                InProgressOrderState.WAITING,
                accountEntity);
        entityManager.persist(inProgressOrderEntity);
        final InProgressOrderLineItemEntity inProgressOrderLineItemEntity = new InProgressOrderLineItemEntity(
                inProgressOrderEntity, bookEntity, bookEntity.getPrice(), 1);
        entityManager.persist(inProgressOrderLineItemEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // When the in-progress order is deleted
        entityManager.getTransaction().begin();
        // For the cascade deletion of the line items to work we need to load them into
        // the entity manager's cache. We therefore fetch them first so that they are
        // included in the deletion.
        final InProgressOrderEntity persistedInProgressOrderEntity = entityManager
                .createQuery("FROM InProgressOrderEntity o LEFT JOIN FETCH o.lineItems WHERE o = :order",
                        InProgressOrderEntity.class)
                .setParameter("order", inProgressOrderEntity)
                .getSingleResult();
        entityManager.remove(persistedInProgressOrderEntity);
        entityManager.getTransaction().commit();

        // Then the in-progress order is absent from the database
        assertNull(entityManager.find(InProgressOrderEntity.class, inProgressOrderEntity.getId()));

        // And the in-progress order line item is absent from the database
        assertNull(entityManager.find(InProgressOrderLineItemEntity.class, inProgressOrderLineItemEntity.getId()));
    }
}
