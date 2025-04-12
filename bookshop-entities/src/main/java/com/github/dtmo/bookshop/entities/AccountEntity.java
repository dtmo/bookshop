package com.github.dtmo.bookshop.entities;

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

@Entity
@Table(name = "account")
@Data
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
    private Set<CustomerEntity> customers = Set.of();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PaymentCardEntity> paymentCardEntities = Set.of();

    @OneToMany(mappedBy = "id.account", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ShoppingBasketLineItemEntity> shopppingBasketLineItems = List.of();
}
