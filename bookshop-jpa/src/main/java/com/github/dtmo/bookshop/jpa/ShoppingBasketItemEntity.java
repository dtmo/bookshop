package com.github.dtmo.bookshop.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * ShoppingBasketItemEntity is the JPA representation of a shopping basket item.
 * A shopping basket item represents a book and quantity that the customer has
 * identified for purchase.
 */
@Entity
@Table(name = "shopping_basket_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShoppingBasketItemEntity {
    /**
     * ShoppingBasketItemId is the JPA representation of the composite primary
     * key of a shopping basket item.
     */
    @Embeddable
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class ShoppingBasketItemId {
        /** The account that owns the shopping basket item. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "account_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private AccountEntity account;

        /** The book referenced by the shopping basket item. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private BookEntity book;
    }

    /** The unique ID of the shopping basket item. */
    @EmbeddedId
    @NonNull
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private ShoppingBasketItemId id;

    /** The quantity of the item added to the shopping basket. */
    @Column(name = "quantity")
    private long quantity;

    /**
     * Constructs a new instance of ShoppingBasketItemEntity.
     * 
     * @param id       The unique ID of the shopping basket item.
     * @param quantity The quantity of the item added to the shopping basket.
     */
    public ShoppingBasketItemEntity(final ShoppingBasketItemId id, final long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    /**
     * Constructs a new instance of ShoppingBasketItemEntity.
     * 
     * @param account  The account that owns the shopping basket item.
     * @param book     The book referenced by the shopping basket item.
     * @param quantity The quantity of the item added to the shopping basket.
     */
    public ShoppingBasketItemEntity(final AccountEntity account, final BookEntity book, final long quantity) {
        this(new ShoppingBasketItemId(account, book), quantity);
    }

    /**
     * Returns the account that owns the shopping basket item.
     * 
     * @return The account that owns the shopping basket item.
     */
    public AccountEntity getAccount() {
        return this.id.getAccount();
    }

    /**
     * Returns the book referenced by the shopping basket item.
     * 
     * @return The book referenced by the shopping basket item.
     */
    public BookEntity getBook() {
        return this.id.getBook();
    }
}
