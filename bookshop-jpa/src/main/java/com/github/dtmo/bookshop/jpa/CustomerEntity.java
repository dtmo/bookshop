package com.github.dtmo.bookshop.jpa;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
 * CustomerEntity is the JPA representation of a customer. A customer represents
 * an individual who will provide authentication credentials to get access to
 * the book shop service. A customer may have access to many accounts.
 */
@Entity
@Table(name = "customer")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CustomerEntity {
    /**
     * The database generated unique ID of the customer. This field is
     * <code>null</code> until the entity is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    /** The time at which the book entity was created. */
    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    /**
     * The customer's name. This may be updated and so cannot be relied on to remain
     * static over time. As such, any reference to a customer should use the
     * customer's ID.
     */
    @Column(name = "name")
    @NonNull
    private String name;

    /**
     * The set of accounts to which the customer has access. The set of associated
     * accounts may change over time as customers are added to or removed from an
     * account. The account is the 'owning' entity in the account / customer
     * many-to-many relationship, so changes to the set of customers should be made
     * through the account entity.
     */
    @ManyToMany(mappedBy = "customers", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<AccountEntity> accounts = new HashSet<>();
}
