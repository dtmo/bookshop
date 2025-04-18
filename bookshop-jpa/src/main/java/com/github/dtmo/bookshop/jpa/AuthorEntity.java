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
import lombok.ToString;

/**
 * AuthorEntity is the JPA representation of an author. An author is associated
 * with one or more books.
 */
@Entity
@Table(name = "author")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuthorEntity {
    /**
     * The database generated unique ID of the author. This field is
     * <code>null</code> until the entity is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    // Different authors can share the same name (e.g. David Mitchell) and so
    // equality can only really be determined based on the "id" field.
    @EqualsAndHashCode.Include
    private Long id;

    /** The time at which the author entity was created. */
    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    /**
     * The author name. There is no expectation that an author's name will be
     * unique, so references to author's should always be made by the author ID.
     */
    @Column(name = "name")
    @NonNull
    private String name;

    /**
     * The set of books associated with the author. The book is the 'owning' entity
     * in the book / author many-to-many relationship, so changes to the set of
     * books should be made by adding the author to the appropriate book entity.
     */
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<BookEntity> books = new HashSet<>();
}
