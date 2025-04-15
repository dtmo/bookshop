package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

public class PaymentCardEntityIntegrationTest extends AbstractIntegrationTest {
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
    public void testPersistPaymentCard() {
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        final PaymentCardEntity expectedPaymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(expectedPaymentCardEntity);

        entityManager.getTransaction().commit();

        entityManager.clear();

        final PaymentCardEntity actualPaymentCardEntity = entityManager.find(PaymentCardEntity.class,
                expectedPaymentCardEntity.getId());

        assertNotSame(expectedPaymentCardEntity, actualPaymentCardEntity);
        PaymentCardEntities.verifyPaymentCardEntity(expectedPaymentCardEntity, actualPaymentCardEntity);
    }

    @Test
    public void testRenamePaymentCard() {
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);

        entityManager.getTransaction().commit();

        final String expectedPaymentCardName = String.format("%s (updated)", paymentCardEntity.getName());

        // Update the payment card name directly in the database
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE PaymentCardEntity p SET p.name = :name WHERE p = :paymentCard")
                .setParameter("paymentCard", paymentCardEntity)
                .setParameter("name", expectedPaymentCardName)
                .executeUpdate();
        entityManager.getTransaction().commit();

        assertNotEquals(paymentCardEntity.getName(), expectedPaymentCardName);

        // Update the payment card details and verify that it now has the expected name
        entityManager.refresh(paymentCardEntity);
        assertEquals(expectedPaymentCardName, paymentCardEntity.getName());
    }

    @Test
    public void testUpdateExpiry() {
        // Given an account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And an account payment card exists
        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);
        entityManager.getTransaction().commit();

        // When the payment card expiry is updated
        final LocalDate updatedExpiry = paymentCardEntity.getExpiry().plusYears(3);
        entityManager.getTransaction().begin();
        paymentCardEntity.setExpiry(updatedExpiry);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the payment card's expiry is updated in the database
        final PaymentCardEntity persistedPaymentCardEntity = entityManager
                .createQuery("FROM PaymentCardEntity p WHERE p = :paymentCard", PaymentCardEntity.class)
                .setParameter("paymentCard", paymentCardEntity)
                .getSingleResult();
        assertEquals(updatedExpiry, persistedPaymentCardEntity.getExpiry());
    }

    @Test
    public void testDeletePaymentCard() {
        // Given an account exists
        entityManager.getTransaction().begin();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        entityManager.persist(accountEntity);

        // And an account payment card exists
        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);
        entityManager.getTransaction().commit();

        // When the payment card is deleted
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE PaymentCardEntity p WHERE p = :paymentCard")
                .setParameter("paymentCard", paymentCardEntity)
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the payment card no longer exists
        assertNull(entityManager.find(PaymentCardEntity.class, paymentCardEntity.getId()));

        // And the payment card is no longer included in the account's payment cards
        final AccountEntity persistedAccountEntity = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.paymentCards WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();
        assertFalse(persistedAccountEntity.getPaymentCards().contains(paymentCardEntity));
    }
}
