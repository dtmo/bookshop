package com.github.dtmo.bookshop.jpa;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * BookEntity is the JPA representation of a book. A book is the product sold by
 * the book shop and is associated with one or more authors.
 */
@Entity
@Table(name = "book")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookEntity {
    /**
     * The database generated unique ID of the book. This field is <code>null</code>
     * until the entity is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    /** The time at which the book entity was created. */
    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    /** The book price in pennies, cents, etc. */
    @Column(name = "price")
    @NonNull
    private Long price;

    /** The unique stock keeping unit (SKU) code of the book. */
    @Column(name = "stock_keeping_unit")
    @NonNull
    private String stockKeepingUnit;

    /** The book title. */
    @Column(name = "title")
    @NonNull
    private String title;

    /** Optional production credits. This may be <code>null</code>. */
    @Column(name = "production_credits")
    private String productionCredits;

    /** Optional summary of the book. This may be <code>null</code>. */
    @Column(name = "summary")
    private String summary;

    /**
     * Optional ISO 639 language code for the book. This may be <code>null</code>.
     */
    @Column(name = "language")
    private String language;

    /**
     * The book's authors. The book is the 'owning' entity
     * in the book / author many-to-many relationship, so adding an author to the
     * set of book authors will have the side-effect of adding the book to the
     * author's set of books.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Setter(AccessLevel.NONE)
    @Singular
    @ToString.Exclude
    private final Set<AuthorEntity> authors = new HashSet<>();
}
