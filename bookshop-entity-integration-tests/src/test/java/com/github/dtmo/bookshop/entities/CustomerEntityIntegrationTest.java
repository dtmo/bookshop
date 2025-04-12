package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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

        final TypedQuery<CustomerEntity> customerQuery = entityManager
                .createQuery("FROM CustomerEntity c WHERE c.name = :name", CustomerEntity.class)
                .setParameter("name", expectedCustomerEntity.getName());
        assertTrue(customerQuery.getResultList().isEmpty());

        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(expectedCustomerEntity);
        transaction.commit();

        // Foget about cached entities so results are loaded from the database
        entityManager.clear();

        final List<CustomerEntity> actualCustomerEntities = customerQuery.getResultList();

        assertEquals(1, actualCustomerEntities.size());

        final CustomerEntity actualCustomerEntity = actualCustomerEntities.get(0);

        assertEquals(expectedCustomerEntity, actualCustomerEntity);
        assertEquals(expectedCustomerEntity.getName(), actualCustomerEntity.getName());
        assertEquals(expectedCustomerEntity.getAccounts(), actualCustomerEntity.getAccounts());
    }
}
