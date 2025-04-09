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

public class AccountEntityIntegrationTest extends AbstractIntegrationTest{
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
    public void testAuthor() {
        final String accountName = "Count Acc";

        final TypedQuery<AccountEntity> accountQuery = entityManager.createQuery("FROM AccountEntity a WHERE a.name = :name", AccountEntity.class);
        accountQuery.setParameter("name", accountName);

        assertTrue(accountQuery.getResultList().isEmpty());

        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        final AccountEntity accountEntity = new AccountEntity();
        accountEntity.setName(accountName);

        entityManager.persist(accountEntity);
        entityTransaction.commit();

        final List<AccountEntity> accounts = accountQuery.getResultList();
        assertEquals(1, accounts.size());

        final AccountEntity actualAccount = accounts.get(0);

        assertEquals(accountEntity.getId(), actualAccount.getId());
        assertEquals(accountEntity.getName(), actualAccount.getName());
    }
}
