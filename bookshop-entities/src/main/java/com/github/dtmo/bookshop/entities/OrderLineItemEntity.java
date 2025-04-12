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
@Table(name = "order_line_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderLineItemEntity {
    @Embeddable
    public static class OrderLineItemId {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "order_id")
        private OrderEntity order;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id")
        private ProductEntity product;
    }

    @EmbeddedId
    @EqualsAndHashCode.Include
    private OrderLineItemId id;

    @Column(name = "quantity")
    @NonNull
    private Long quantity;

    @Column(name = "unit_price")
    @NonNull
    private Long unitPrice;
}
