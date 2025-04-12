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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "shopping_basket_line_item")
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
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
        @NonNull
        private AccountEntity account;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id")
        @NonNull
        private ProductEntity product;
    }

    @EmbeddedId
    @EqualsAndHashCode.Include
    private ShoppingBasketLineItemId id;

    @Column(name = "quantity")
    @NonNull
    private Long quantity;
}
