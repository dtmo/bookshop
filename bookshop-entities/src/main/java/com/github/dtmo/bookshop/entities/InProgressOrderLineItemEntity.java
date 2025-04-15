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

@Entity
@Table(name = "in_progress_order_line_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InProgressOrderLineItemEntity {
    @Embeddable
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class InProgressOrderLineItemId {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "in_progress_order_id")
        @NonNull
        private InProgressOrderEntity inProgressOrder;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id")
        @NonNull
        private ProductEntity product;
    }

    @EmbeddedId
    @NonNull
    @EqualsAndHashCode.Include
    private InProgressOrderLineItemId id;

    @Column(name = "unit_price")
    private long unitPrice;

    @Column(name = "quantity")
    private long quantity;

    public InProgressOrderLineItemEntity(final InProgressOrderLineItemId id, final long unitPrice,
            final long quantity) {
        this.id = id;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public InProgressOrderLineItemEntity(final InProgressOrderEntity inProgressOrder, final ProductEntity product,
            final long unitPrice, final long quantity) {
        this(new InProgressOrderLineItemId(inProgressOrder, product), unitPrice, quantity);
    }

    public InProgressOrderEntity getInProgressOrder() {
        return this.id.getInProgressOrder();
    }

    public ProductEntity getProduct() {
        return this.id.getProduct();
    }
}
