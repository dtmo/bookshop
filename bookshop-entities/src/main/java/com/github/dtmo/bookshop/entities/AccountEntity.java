package com.github.dtmo.bookshop.entities;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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

@Entity
@Table(name = "account")
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountEntity {
    // While accounts should have unique names, an account could be renamed, which
    // would make the name a terrible natural key, so we determine equality based on
    // the ID field.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "creation_time")
    @Builder.Default
    private Instant creationTime = Instant.now();

    @Column(name = "name")
    @NonNull
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_customers", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "customer_id"))
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<CustomerEntity> customers = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<PaymentCardEntity> paymentCards = new HashSet<>();

    @OneToMany(mappedBy = "id.account", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private final Set<ShoppingBasketLineItemEntity> shopppingBasketLineItems = new HashSet<>();
}
