package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class AuthorEntityIntegrationTest extends AbstractIntegrationTest {
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
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();

        final TypedQuery<AuthorEntity> authorQuery = entityManager
                .createQuery("FROM AuthorEntity a WHERE a.name = :name", AuthorEntity.class);
        authorQuery.setParameter("name", authorEntity.getName());

        assertTrue(authorQuery.getResultList().isEmpty());

        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(authorEntity);
        entityTransaction.commit();

        final List<AuthorEntity> authors = authorQuery.getResultList();
        assertEquals(1, authors.size());

        final AuthorEntity actualAuthor = authors.get(0);

        assertEquals(authorEntity.getId(), actualAuthor.getId());
        assertEquals(authorEntity.getName(), actualAuthor.getName());
        assertEquals(authorEntity.getAlias(), actualAuthor.getAlias());
    }
}
