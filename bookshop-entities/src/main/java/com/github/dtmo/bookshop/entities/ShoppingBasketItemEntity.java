package com.github.dtmo.bookshop.entities;

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

@Entity
@Table(name = "shopping_basket_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShoppingBasketItemEntity {
    @Embeddable
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class ShoppingBasketItemId {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "account_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private AccountEntity account;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private BookEntity book;
    }

    @EmbeddedId
    @NonNull
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private ShoppingBasketItemId id;

    @Column(name = "quantity")
    private long quantity;

    public ShoppingBasketItemEntity(final ShoppingBasketItemId id, final long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public ShoppingBasketItemEntity(final AccountEntity account, final BookEntity book, final long quantity) {
        this(new ShoppingBasketItemId(account, book), quantity);
    }

    public AccountEntity getAccount() {
        return this.id.getAccount();
    }

    public BookEntity getBook() {
        return this.id.getBook();
    }
}
