package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

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

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAuthor);
        entityManager.getTransaction().commit();

        entityManager.clear();

        final AuthorEntity actualAuthor = entityManager.find(AuthorEntity.class, expectedAuthor.getId());

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

        assertNotEquals(authorEntity.getName(), expectedAuthorName);

        // Update the author details and verify that it now has the expected name
        entityManager.refresh(authorEntity);
        assertEquals(expectedAuthorName, authorEntity.getName());
    }

    @Test
    public void testAddBook() {
        entityManager.getTransaction().begin();
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);

        final BookEntity bookEntity = getBookEntitySupplier().get();
        entityManager.persist(bookEntity);
        bookEntity.getAuthors().add(authorEntity);
        entityManager.getTransaction().commit();

        // The author has published a new book
        entityManager.getTransaction().begin();
        final BookEntity newBookEntity = getBookEntitySupplier().get();
        newBookEntity.getAuthors().add(authorEntity);
        entityManager.persist(newBookEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Assert that the author now has two books
        final AuthorEntity persistedAuthorEntity = entityManager
                .createQuery("FROM AuthorEntity a LEFT JOIN FETCH a.books WHERE a = :author", AuthorEntity.class)
                .setParameter("author", authorEntity)
                .getSingleResult();
        assertEquals(2, persistedAuthorEntity.getBooks().size());
        assertTrue(persistedAuthorEntity.getBooks().contains(bookEntity));
        assertTrue(persistedAuthorEntity.getBooks().contains(newBookEntity));

        final BookEntity persistedBookEntity = entityManager
                .createQuery("FROM BookEntity b LEFT JOIN FETCH b.authors WHERE b = :book", BookEntity.class)
                .setParameter("book", bookEntity)
                .getSingleResult();
        assertTrue(persistedBookEntity.getAuthors().contains(authorEntity));
    }

    @Test
    public void testDeleteAuthor() {
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        final BookEntity bookEntity = getBookEntitySupplier().get();

        entityManager.getTransaction().begin();
        entityManager.persist(authorEntity);
        entityManager.persist(bookEntity);
        bookEntity.getAuthors().add(authorEntity);
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        // To delete an author we must first remove them from their books
        entityManager
                .createQuery("FROM BookEntity b LEFT JOIN FETCH b.authors a WHERE a = :author", BookEntity.class)
                .setParameter("author", authorEntity).getResultList()
                .forEach(book -> book.getAuthors().remove(authorEntity));
        // We can then delete the author entity
        entityManager.createQuery("DELETE AuthorEntity a WHERE a = :author")
                .setParameter("author", authorEntity)
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Verify that the author has been deleted
        assertNull(entityManager.find(AuthorEntity.class, authorEntity.getId()));

        // Verify that the book has not been deleted
        assertNotNull(entityManager.find(BookEntity.class, bookEntity.getId()));
    }
}
