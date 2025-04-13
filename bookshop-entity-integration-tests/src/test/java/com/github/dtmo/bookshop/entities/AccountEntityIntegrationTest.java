package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class AccountEntityIntegrationTest extends AbstractIntegrationTest {
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
    public void testCreateAccount() {
        final AccountEntity expectedAccountEntity = getAccountEntitysupplier().get();

        // Assert that the account does not exist in the database
        final TypedQuery<AccountEntity> accountQuery = entityManager
                .createQuery("FROM AccountEntity a WHERE a.name = :name", AccountEntity.class)
                .setParameter("name", expectedAccountEntity.getName());
        assertTrue(accountQuery.getResultList().isEmpty());

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAccountEntity);
        entityManager.getTransaction().commit();

        // Clear the entity manager so we don't hit cached entities
        entityManager.clear();

        final AccountEntity actualAccountEntity = entityManager.find(AccountEntity.class,
                expectedAccountEntity.getId());

        assertNotSame(expectedAccountEntity, actualAccountEntity);

        AccountEntities.verifyAccountEntity(expectedAccountEntity, actualAccountEntity);
    }

    @Test
    public void testSettingCustomerAccounts() {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // Given a customer exists
        final CustomerEntity customer = getCustomerEntitySupplier().get();
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = getAccountEntitysupplier().get();
        entityManager.persist(account);

        entityTransaction.commit();

        // And the customer and account are not linked
        assertTrue(customer.getAccounts().isEmpty());
        assertTrue(account.getCustomers().isEmpty());

        // When the account is associated with the customer
        entityTransaction.begin();
        customer.getAccounts().add(account);
        entityTransaction.commit();

        // Then the customer accounts includes the account
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the account customers includes the customer
        entityManager.refresh(account);
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }

    @Test
    public void testRenamingAnAccount() {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // Given a customer exists
        final CustomerEntity customer = getCustomerEntitySupplier().get();
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = getAccountEntitysupplier().get();
        entityManager.persist(account);

        // And the customer and account are linked
        customer.getAccounts().add(account);

        entityTransaction.commit();

        // When the account is renamed
        entityTransaction.begin();
        // (Mr. Norrell made the statues in York Cathedral speak)
        account.setName(String.format("%s (updated)", account.getName()));
        entityTransaction.commit();

        // Then the customer accounts contains the renamed account
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the renamed account customers contains the customer
        entityManager.refresh(account);
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }
}
