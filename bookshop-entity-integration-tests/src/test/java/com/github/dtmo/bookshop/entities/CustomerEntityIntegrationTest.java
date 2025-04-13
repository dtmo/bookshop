package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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

        // Assert that the customer entity does not already exist in the database
        final TypedQuery<CustomerEntity> customerQuery = entityManager
                .createQuery("FROM CustomerEntity c WHERE c.name = :name", CustomerEntity.class)
                .setParameter("name", expectedCustomerEntity.getName());
        assertTrue(customerQuery.getResultList().isEmpty());

        // Persist the customer entity
        entityManager.getTransaction().begin();
        entityManager.persist(expectedCustomerEntity);
        entityManager.getTransaction().commit();

        // Foget about cached entities so results are loaded from the database
        entityManager.clear();

        // Re-execute the query which should now find our entity
        final List<CustomerEntity> actualCustomerEntities = customerQuery.getResultList();

        assertEquals(1, actualCustomerEntities.size());

        final CustomerEntity actualCustomerEntity = actualCustomerEntities.get(0);

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
        // We need do this after persistence or JPA will not update the join table
        customerEntity.getAccounts().add(accountEntity);
        entityManager.getTransaction().commit();

        // Forget all the cached entity instances so everything that follows is
        // based on the persisted data
        entityManager.clear();

        // Read the customer and account entities and assert that the customer
        // accounts contains the account, and that the account customers contains
        // the customer
        final CustomerEntity persistedCustomerEntity = entityManager.find(CustomerEntity.class, customerEntity.getId());
        assertNotNull(persistedCustomerEntity);

        final AccountEntity persistedAccountEntity = entityManager.find(AccountEntity.class, accountEntity.getId());
        assertNotNull(persistedAccountEntity);

        assertTrue(persistedCustomerEntity.getAccounts().contains(persistedAccountEntity));
        assertTrue(persistedAccountEntity.getCustomers().contains(persistedCustomerEntity));

        // Delete the customer
        entityManager.getTransaction().begin();
        entityManager.remove(persistedCustomerEntity);
        entityManager.getTransaction().commit();

        // Update the account and assert that its customers no longer contains
        // the deleted customer
        entityManager.refresh(persistedAccountEntity);
        assertFalse(persistedAccountEntity.getCustomers().contains(persistedCustomerEntity));

        // Assert that we can no longer find the customer in the database
        assertNull(entityManager.find(CustomerEntity.class, persistedCustomerEntity.getId()));
    }
}
