package com.github.dtmo.bookshop;

import java.util.Optional;

import com.github.dtmo.bookshop.jpa.AccountEntity;
import com.github.dtmo.bookshop.jpa.BookEntity;
import com.github.dtmo.bookshop.jpa.CustomerEntity;
import com.github.dtmo.bookshop.jpa.ShoppingBasketItemEntity;
import com.google.common.flogger.FluentLogger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class BookshopService {
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    private final AuditLogger auditLogger;
    private final EntityManagerFactory entityManagerFactory;

    public BookshopService(final AuditLogger auditLogger, final EntityManagerFactory entityManagerFactory) {
        this.auditLogger = auditLogger;
        this.entityManagerFactory = entityManagerFactory;
    }

    public ShoppingBasketItem addItemToShoppingBasket(final User user, final long accountId,
            final long bookId, final long quantity) throws AuthorizationException {
        try (final EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            // Look up the account to make sure it exists.
            final AccountEntity accountEntity = entityManager.getReference(AccountEntity.class, accountId);

            // Find a user with the specified username and access to the specified account.
            final Optional<CustomerEntity> customerEntity = Optional.ofNullable(entityManager
                    .createQuery("FROM CustomerEntity c WHERE c.username = :username AND :account IN c.accounts",
                            CustomerEntity.class)
                    .setParameter("username", user.getId())
                    .setParameter("account", accountEntity)
                    .getSingleResultOrNull());

            if (customerEntity.isPresent()) {
                final BookEntity bookEntity = entityManager.getReference(BookEntity.class, bookId);
                final ShoppingBasketItemEntity shoppingBasketItemEntity = new ShoppingBasketItemEntity(accountEntity,
                        bookEntity, quantity);

                entityManager.getTransaction().begin();
                entityManager.merge(shoppingBasketItemEntity);
                entityManager.getTransaction().commit();
                auditLogger.logAuditEvent(user, AuditEvent.builder()
                        .action("Set shopping basket item")
                        .parameter("accountId", String.valueOf(accountId))
                        .parameter("bookId", String.valueOf(bookId))
                        .parameter("quantity", String.valueOf(quantity))
                        .build());
                return ShoppingBasketItem.builder()
                        .accountId(accountId)
                        .bookId(bookId)
                        .quantity(quantity)
                        .build();
            } else {
                auditLogger.logAuditEvent(user, AuditEvent.builder()
                        .action("Set shopping basket item")
                        .parameter("accountId", String.valueOf(accountId))
                        .parameter("bookId", String.valueOf(bookId))
                        .parameter("quantity", String.valueOf(quantity))
                        .error("not authorized to access account")
                        .build());
                throw new AuthorizationException(
                        String.format("User %s does not have access to update account %s shopping basket",
                                user.getId(), accountId));
            }
        }
    }
}
