package com.github.dtmo.bookshop.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

public class CustomerEntityIntegrationTest extends AbstractIntegrationTest {
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
    public void testPersistCustomerEntity() {
        final CustomerEntity expectedCustomerEntity = getCustomerEntitySupplier().get();

        // Persist the customer entity
        entityManager.getTransaction().begin();
        entityManager.persist(expectedCustomerEntity);
        entityManager.getTransaction().commit();

        // Foget about cached entities so results are loaded from the database
        entityManager.clear();

        // Find the persisted entity
        final CustomerEntity actualCustomerEntity = entityManager.find(CustomerEntity.class,
                expectedCustomerEntity.getId());

        // Assert that the original and loaded entities are not the same, so we
        // can be confident that we have the values from the database
        assertNotSame(expectedCustomerEntity, actualCustomerEntity);

        CustomerEntities.verifyCustomerEntity(expectedCustomerEntity, actualCustomerEntity);
    }

    @Test
    public void testRenameCustomerEntity() {
        // Create a customer entity
        final CustomerEntity expectedCustomerEntity = getCustomerEntitySupplier().get();

        // Persist it in the database
        entityManager.getTransaction().begin();
        entityManager.persist(expectedCustomerEntity);
        entityManager.getTransaction().commit();

        // Change the name and update the database
        entityManager.getTransaction().begin();
        expectedCustomerEntity.setName(String.format("%s (updated)", expectedCustomerEntity.getName()));
        entityManager.getTransaction().commit();

        // Forget about the initial entity to avoid finding the cached instance
        entityManager.clear();

        // Find the entity with the matching ID
        final CustomerEntity actualCustomerEntity = entityManager.find(CustomerEntity.class,
                expectedCustomerEntity.getId());

        // Assert that the original and loaded entities are not the same, so we
        // can be confident that we have the values from the database
        assertNotSame(expectedCustomerEntity, actualCustomerEntity);

        // Assert that the values are as expected
        CustomerEntities.verifyCustomerEntity(expectedCustomerEntity, actualCustomerEntity);
    }

    @Test
    public void testDeleteCustomerEntity() {
        // Create a customer and an account
        final CustomerEntity customerEntity = getCustomerEntitySupplier().get();
        final AccountEntity accountEntity = new AccountEntity(String.format("%s Account", customerEntity.getName()));

        entityManager.getTransaction().begin();
        entityManager.persist(customerEntity);
        entityManager.persist(accountEntity);

        // Add the customer to the account
        accountEntity.getCustomers().add(customerEntity);
        entityManager.getTransaction().commit();

        // Forget all the cached entity instances so everything that follows is
        // based on the persisted data
        entityManager.clear();

        // Read the customer and account entities and assert that the customer
        // accounts contains the account, and that the account customers contains
        // the customer
        final List<AccountEntity> actualCustomerAccounts = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.customers c WHERE c = :customer",
                        AccountEntity.class)
                .setParameter("customer", customerEntity)
                .getResultList();
        assertFalse(actualCustomerAccounts.isEmpty());

        final CustomerEntity actualCustomerEntity = entityManager.find(CustomerEntity.class,
                customerEntity.getId());

        // Delete the customer
        entityManager.getTransaction().begin();
        // As AccountEntity owns the Account / Customer relationship, we need to first
        // remove the customer from all accounts before we can delete the customer
        actualCustomerAccounts.forEach(customerAccount -> customerAccount.getCustomers().remove(customerEntity));
        entityManager.remove(actualCustomerEntity);
        entityManager.getTransaction().commit();

        // Assert that we can no longer find the customer in the database
        assertNull(entityManager.find(CustomerEntity.class, customerEntity.getId()));
    }
}
