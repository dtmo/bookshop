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
@Table(name = "shopping_basket_line_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ShoppingBasketLineItemEntity {
    @Embeddable
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class ShoppingBasketLineItemId {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "account_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private AccountEntity account;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id")
        @Setter(AccessLevel.NONE)
        @NonNull
        private ProductEntity product;
    }

    @EmbeddedId
    @NonNull
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private ShoppingBasketLineItemId id;

    @Column(name = "quantity")
    private long quantity;

    public ShoppingBasketLineItemEntity(final ShoppingBasketLineItemId id, final long quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public ShoppingBasketLineItemEntity(final AccountEntity account, final ProductEntity product, final long quantity) {
        this(new ShoppingBasketLineItemId(account, product), quantity);
    }

    public AccountEntity getAccount() {
        return this.id.getAccount();
    }

    public ProductEntity getProduct() {
        return this.id.getProduct();
    }
}
