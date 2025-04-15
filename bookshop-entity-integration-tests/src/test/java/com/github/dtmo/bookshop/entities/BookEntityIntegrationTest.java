package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;

public class BookEntityIntegrationTest extends AbstractIntegrationTest {
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
    public void testPersistBook() {
        final AuthorEntity expectedAuthor = getAuthorEntitysupplier().get();
        final BookEntity expectedBook = getBookEntitySupplier().get();
        expectedBook.getAuthors().add(expectedAuthor);

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAuthor);
        entityManager.persist(expectedBook);
        entityManager.getTransaction().commit();

        entityManager.clear();

        final BookEntity actualBook = entityManager.find(BookEntity.class, expectedBook.getId());

        assertNotSame(expectedBook, actualBook);
        BookEntities.verifyBookEntity(expectedBook, actualBook);
    }

    @Test
    public void testRenameBook() {
        entityManager.getTransaction().begin();
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);

        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        final String expectedBookTitle = String.format("%s (updated)", bookEntity.getTitle());

        // Update the book title directly in the database
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE BookEntity b SET b.title = :title WHERE b = :book")
                .setParameter("book", bookEntity)
                .setParameter("title", expectedBookTitle)
                .executeUpdate();
        entityManager.getTransaction().commit();

        assertNotEquals(bookEntity.getTitle(), expectedBookTitle);

        // Update the book details and verify that it now has the expected title
        entityManager.refresh(bookEntity);
        assertEquals(expectedBookTitle, bookEntity.getTitle());
    }

    @Test
    public void testAddAuthor() {
        // Given an author exists
        entityManager.getTransaction().begin();
        final AuthorEntity creditedAuthorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(creditedAuthorEntity);

        // And a book exists written by the author
        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(creditedAuthorEntity);
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        // When a new author is added to the existing book
        entityManager.getTransaction().begin();
        final AuthorEntity uncreditedAuthorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(uncreditedAuthorEntity);

        bookEntity.getAuthors().add(uncreditedAuthorEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the new author's books contains the book
        final AuthorEntity persistedUncreditedAuthorEntity = entityManager
                .createQuery("FROM AuthorEntity a LEFT JOIN FETCH a.books WHERE a = :author", AuthorEntity.class)
                .setParameter("author", uncreditedAuthorEntity)
                .getSingleResult();
        assertTrue(persistedUncreditedAuthorEntity.getBooks().contains(bookEntity));

        // And the book's authors includes the new author
        final BookEntity persistedBookEntity = entityManager
                .createQuery("FROM BookEntity b LEFT JOIN FETCH b.authors WHERE b = :book", BookEntity.class)
                .setParameter("book", bookEntity)
                .getSingleResult();
        assertEquals(2, persistedBookEntity.getAuthors().size());
        assertTrue(persistedBookEntity.getAuthors().contains(creditedAuthorEntity));
        assertTrue(persistedBookEntity.getAuthors().contains(uncreditedAuthorEntity));
    }

    @Test
    public void testRemoveAuthor() {
        // Given an author exists who will be retained in the book's authors
        entityManager.getTransaction().begin();
        final AuthorEntity authorToRetainEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorToRetainEntity);

        // And an author exists who will be removed from the book's authors
        final AuthorEntity authorToRemoveEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorToRemoveEntity);

        // And a book exists with both the authors
        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorToRetainEntity);
        bookEntity.getAuthors().add(authorToRemoveEntity);
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        // When the author to be removed is removed from the book's authors
        entityManager.getTransaction().begin();
        bookEntity.getAuthors().remove(authorToRemoveEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the book is no longer included in the removed author's books
        final AuthorEntity persistedAuthorToRemoveEntity = entityManager
                .createQuery("FROM AuthorEntity a LEFT JOIN FETCH a.books WHERE a = :author", AuthorEntity.class)
                .setParameter("author", authorToRemoveEntity)
                .getSingleResult();
        assertFalse(persistedAuthorToRemoveEntity.getBooks().contains(bookEntity));

        // And the removed author is no longer included in the book's authors
        final BookEntity persistedBookEntity = entityManager
                .createQuery("FROM BookEntity b LEFT JOIN FETCH b.authors WHERE b = :book", BookEntity.class)
                .setParameter("book", bookEntity)
                .getSingleResult();
        assertEquals(1, persistedBookEntity.getAuthors().size());
        assertTrue(persistedBookEntity.getAuthors().contains(authorToRetainEntity));
        assertFalse(persistedBookEntity.getAuthors().contains(authorToRemoveEntity));
    }

    @Test
    public void testDeleteBook() {
        // Given an author exists
        entityManager.getTransaction().begin();
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);

        // And a book exists with the author in the book's authors
        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(bookEntity);
        entityManager.getTransaction().commit();

        // When the book is deleted
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE BookEntity b WHERE b = :book")
                .setParameter("book", bookEntity)
                .executeUpdate();
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Then the book no longer exists
        assertNull(entityManager.find(BookEntity.class, bookEntity.getId()));

        // And the book is no longer included in the authors books
        final AuthorEntity persistedAuthorEntity = entityManager
                .createQuery("FROM AuthorEntity a LEFT JOIN FETCH a.books WHERE a = :author", AuthorEntity.class)
                .setParameter("author", authorEntity)
                .getSingleResult();
        assertFalse(persistedAuthorEntity.getBooks().contains(bookEntity));
    }
}
