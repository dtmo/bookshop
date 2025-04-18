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

/**
 * InProgressOrderLineItemEntity is the JPA representation of an in-progress
 * order line item. An in-progress order line item represents the quantity and
 * price of an ordered book.
 */
@Entity
@Table(name = "in_progress_order_line_item")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InProgressOrderLineItemEntity {
    /**
     * InProgressOrderLineItemId is the JPA representation of the composite primary
     * key of an in-progress order line item.
     */
    @Embeddable
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static class InProgressOrderLineItemId {
        /** The in-progress order that owns the line item. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "in_progress_order_id")
        @NonNull
        private InProgressOrderEntity inProgressOrder;

        /** The book referenced by the line item. */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "book_id")
        @NonNull
        private BookEntity book;
    }

    /** The unique ID of the in-progress order line item. */
    @EmbeddedId
    @NonNull
    @EqualsAndHashCode.Include
    private InProgressOrderLineItemId id;

    /** The unit price of the ordered book at the point where it was ordered. */
    @Column(name = "unit_price")
    private long unitPrice;

    /** The quantity of the referenced book ordered. */
    @Column(name = "quantity")
    private long quantity;

    /**
     * Constructs a new instance of InProgressOrderLineItemEntity.
     * 
     * @param id        The unique ID of the in-progress order line item.
     * @param unitPrice The unit price of the ordered book at the point where it was
     *                  ordered.
     * @param quantity  The quantity of the referenced book ordered.
     */
    public InProgressOrderLineItemEntity(final InProgressOrderLineItemId id, final long unitPrice,
            final long quantity) {
        this.id = id;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /**
     * Constructs a new instance of InProgressOrderLineItemEntity.
     * 
     * @param inProgressOrder The in-progress order that owns the line item.
     * @param book            The book referenced by the line item.
     * @param unitPrice       The unit price of the ordered book at the point where
     *                        it was ordered.
     * @param quantity        The quantity of the referenced book ordered.
     */
    public InProgressOrderLineItemEntity(final InProgressOrderEntity inProgressOrder, final BookEntity book,
            final long unitPrice, final long quantity) {
        this(new InProgressOrderLineItemId(inProgressOrder, book), unitPrice, quantity);
    }

    /**
     * Returns the in-progress order that owns the line item.
     * 
     * @return The in-progress order that owns the line item.
     */
    public InProgressOrderEntity getInProgressOrder() {
        return this.id.getInProgressOrder();
    }

    /**
     * Returns the book referenced by the line item.
     * 
     * @return The book referenced by the line item.
     */
    public BookEntity getBook() {
        return this.id.getBook();
    }
}
