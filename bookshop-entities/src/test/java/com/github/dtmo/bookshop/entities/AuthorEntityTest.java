package com.github.dtmo.bookshop.entities;

import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class AuthorEntityTest extends AbstractIntegrationTest{

    @Test
    public void testAuthor() {
        final EntityManager entityManager = getEntityManagerFactory().createEntityManager();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setName("Earnest Scribbler");

        entityManager.persist(authorEntity);
        entityTransaction.commit();
    }
}
