package com.github.dtmo.bookshop.entities;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "order")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderEntity {
    public static enum OrderState {
        WAITING,
        PROCESSING,
        DISPATCHED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "creation_time")
    @NonNull
    private Instant creationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_card_id")
    @NonNull
    private PaymentCardEntity paymentCard;

    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    @NonNull
    private OrderState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountEntity account;

    @OneToMany(mappedBy = "id.order", fetch = FetchType.LAZY)
    private List<OrderLineItemEntity> lineItems;
}
