package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class AccountEntityTest extends AbstractIntegrationTest{

    @Test
    public void testAuthor() {
        final EntityManager entityManager = getEntityManagerFactory().createEntityManager();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setName("Count Acc");

        entityManager.persist(accountEntity);
        entityTransaction.commit();
    }
}
