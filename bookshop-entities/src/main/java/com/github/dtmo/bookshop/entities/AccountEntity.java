package com.github.dtmo.bookshop.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name")
    @NonNull
    private String name;

    @ManyToMany(mappedBy = "accounts", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<CustomerEntity> customers = new HashSet<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private Set<PaymentCardEntity> paymentCardEntities = new HashSet<>();

    @OneToMany(mappedBy = "id.account", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ShoppingBasketLineItemEntity> shopppingBasketLineItems = new ArrayList<>();
}
