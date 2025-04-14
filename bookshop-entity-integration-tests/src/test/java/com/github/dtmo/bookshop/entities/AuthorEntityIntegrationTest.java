package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
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
    public void testPersistAuthor() {
        final AuthorEntity expectedAuthor = getAuthorEntitysupplier().get();

        final TypedQuery<AuthorEntity> authorQuery = entityManager
                .createQuery("FROM AuthorEntity a WHERE a.name = :name", AuthorEntity.class);
        authorQuery.setParameter("name", expectedAuthor.getName());

        assertTrue(authorQuery.getResultList().isEmpty());

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAuthor);
        entityManager.getTransaction().commit();

        entityManager.clear();

        final AuthorEntity actualAuthor = authorQuery.getSingleResult();

        assertNotSame(expectedAuthor, actualAuthor);
        AuthorEntities.verifyAuthorEntity(expectedAuthor, actualAuthor);
    }

    @Test
    public void testRenameAuthor() {
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        final BookEntity bookEntity = getBookEntitySupplier().get();

        entityManager.getTransaction().begin();
        entityManager.persist(authorEntity);
        entityManager.persist(bookEntity);
        bookEntity.getAuthors().add(authorEntity);
        entityManager.getTransaction().commit();

        final String expectedAuthorName = String.format("%s (updated)", authorEntity.getName());

        // Update the author name directly in the database
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE AuthorEntity a SET a.name = :name WHERE a = :author")
                .setParameter("author", authorEntity)
                .setParameter("name", expectedAuthorName)
                .executeUpdate();
        entityManager.getTransaction().commit();

        // Verify that the detached entity has not been updated
        assertNotEquals(authorEntity.getName(), expectedAuthorName);

        // Update the author details and verify that it now has the expected name
        entityManager.refresh(authorEntity);
        assertEquals(expectedAuthorName, authorEntity.getName());
    }
}
