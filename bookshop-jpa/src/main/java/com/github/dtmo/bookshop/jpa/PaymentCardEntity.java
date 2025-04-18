package com.github.dtmo.bookshop.jpa;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import lombok.Setter;

/**
 * PaymentCardEntity is the JPA representation of a payment card. A payment card
 * represents the details of a card that may be used to pay for ordered books.
 */
@Entity
@Table(name = "payment_card")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentCardEntity {
    /**
     * The database generated unique ID of the payment card. This field is
     * <code>null</code> until the entity is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    /** The time at which the payment card entity was created. */
    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    /** The name, assigned by the customer, to the payment card. */
    @Column(name = "name")
    @NonNull
    private String name;

    /** The name of the payment card cardholder. */
    @Column(name = "cardholder_name")
    @NonNull
    private String cardholderName;

    /** The payment card number. */
    @Column(name = "card_number")
    @NonNull
    private String cardNumber;

    /** The date at which the payment card expires. */
    @Column(name = "expiry")
    @NonNull
    private LocalDate expiry;

    /** The account to which the payment card belongs. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @NonNull
    private AccountEntity account;
}
