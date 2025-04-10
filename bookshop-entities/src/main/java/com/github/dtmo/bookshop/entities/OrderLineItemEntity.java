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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "order_line_item")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
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
    private OrderLineItemId id;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "unit_price")
    private long unitPrice;
}
