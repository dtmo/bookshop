package com.github.dtmo.bookshop.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "book")
@PrimaryKeyJoinColumn(name = "product_id")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@ToString
@SuperBuilder
public class BookEntity extends ProductEntity {
    @Column(name = "title")
    @NonNull
    private String title;

    @Column(name = "production_credits")
    private String productionCredits;

    @Column(name = "summary")
    private String summary;

    @Column(name = "language")
    private String language;

    @Column(name = "subject")
    private String subject;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @Setter(AccessLevel.NONE)
    @Singular
    @ToString.Exclude
    private final Set<AuthorEntity> authors = new HashSet<>();

    public BookEntity(final Long price, final String stockKeepingUnit, final String title) {
        super(price, stockKeepingUnit);

        this.title = title;
    }
}
