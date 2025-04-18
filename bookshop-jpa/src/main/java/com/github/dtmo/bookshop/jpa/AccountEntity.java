package com.github.dtmo.bookshop.jpa;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

/**
 * AccountEntity is the JPA representation of an account. An account may be
 * accessed by many customers, and is the entity to which payment cards,
 * shopping basket items, and order are associated.
 */
@Entity
@Table(name = "account")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountEntity {
    /**
     * The database generated unique ID of the account. This field is
     * <code>null</code> until the entity is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    // While accounts should have unique names, an account could be renamed, which
    // would make the name a terrible natural key, so we determine equality based on
    // the ID field.
    @EqualsAndHashCode.Include
    private Long id;

    /** The time at which the account entity was created. */
    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    /**
     * The unique name of the account. While this value should be unique within the
     * system, it may be changed and so cannot be relied on to remain static. As
     * such, references to accounts should always be made by the account ID.
     */
    @Column(name = "name")
    @NonNull
    private String name;

    /**
     * The customers who are able to access the account. The set of associated
     * customers may change over time as customers are added to or removed from the
     * account. The account is the 'owning' entity in the account / customer
     * many-to-many relationship, so changes to this set will have the side-effect
     * of updating the set of accounts that the corresponding customer has access
     * to.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_customers", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "customer_id"))
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<CustomerEntity> customers = new HashSet<>();

    /**
     * The payment cards associated with the account. A valid (in the sense of being
     * able to be used with a payment service provider) payment card is required
     * when an account's shopping basket items are converted to an in-progress
     * order. Payment cards associated with an account are expected to expire over
     * time, so not all associated payment cards may be valid forms of payment.
     */
    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<PaymentCardEntity> paymentCards = new HashSet<>();

    /**
     * Shopping basket items are books that the user has selected to purchase some
     * quantity of. The set of items is associated with an account and may be
     * converted into an in-progress order when combined with a valid payment card.
     */
    @OneToMany(mappedBy = "id.account", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<ShoppingBasketItemEntity> shoppingBasketItems = new HashSet<>();

    /**
     * In-progress orders represent requests to purchase books. An order remains
     * in-progress until payment is received, and the books have been dispatched.
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<InProgressOrderEntity> inProgressOrders = new HashSet<>();
}
