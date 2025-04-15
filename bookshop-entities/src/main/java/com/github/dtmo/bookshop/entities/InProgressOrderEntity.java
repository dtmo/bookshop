package com.github.dtmo.bookshop.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "in_progress_order")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InProgressOrderEntity {
    public static enum InProgressOrderState {
        WAITING,
        PROCESSING,
        DISPATCHED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_card_id")
    @NonNull
    private PaymentCardEntity paymentCard;

    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    @NonNull
    @Builder.Default
    private InProgressOrderState state = InProgressOrderState.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountEntity account;

    @OneToMany(mappedBy = "id.inProgressOrder", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<InProgressOrderLineItemEntity> lineItems = new HashSet<>();
}
