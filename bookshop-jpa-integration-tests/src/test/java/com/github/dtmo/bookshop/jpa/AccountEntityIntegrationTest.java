package com.github.dtmo.bookshop.jpa;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AccountEntityIntegrationTest extends AbstractIntegrationTest {
    private EntityManager entityManager;

    @BeforeEach
    public void beforeEach() {
        this.entityManager = AbstractIntegrationTest.getEntityManagerFactory().createEntityManager();
    }

    @AfterEach
    public void afterEach() {
        entityManager.close();
    }

    @Test
    public void testPersistAccount() {
        final AccountEntity expectedAccountEntity = getAccountEntitysupplier().get();

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAccountEntity);
        entityManager.getTransaction().commit();

        // Clear the entity manager so we don't hit cached entities
        entityManager.clear();

        final AccountEntity actualAccountEntity = entityManager.find(AccountEntity.class,
                expectedAccountEntity.getId());

        assertNotSame(expectedAccountEntity, actualAccountEntity);
        AccountEntities.verifyAccountEntity(expectedAccountEntity, actualAccountEntity);
    }

    @Test
    public void testAddCustomers() {
        final AccountEntity accountEntity = getAccountEntitysupplier().get();

        final List<CustomerEntity> customerEntities = Stream.generate(getCustomerEntitySupplier())
                .limit(2)
                .collect(Collectors.toList());

        entityManager.getTransaction().begin();
        entityManager.persist(accountEntity);
        customerEntities.forEach(entityManager::persist);
        entityManager.getTransaction().commit();

        entityManager.refresh(accountEntity);
        assertTrue(accountEntity.getCustomers().isEmpty());

        entityManager.getTransaction().begin();
        accountEntity.getCustomers().addAll(customerEntities);
        entityManager.getTransaction().commit();

        entityManager.refresh(accountEntity);

        assertEquals(customerEntities.size(), accountEntity.getCustomers().size());
        customerEntities.forEach(customerEntity -> accountEntity.getCustomers().contains(customerEntity));
    }

    @Test
    public void testRemoveCustomers() {
        // Create an account with two customers
        entityManager.getTransaction().begin();

        final CustomerEntity expectedCustomerToRemove = getCustomerEntitySupplier().get();
        final CustomerEntity expectedCustomerToRetain = getCustomerEntitySupplier().get();

        // Persisting the customers sets their IDs which means that we can then add them
        // to the account customers set.
        entityManager.persist(expectedCustomerToRemove);
        entityManager.persist(expectedCustomerToRetain);

        final AccountEntity expectedAccountEntity = getAccountEntitysupplier().get();
        expectedAccountEntity.getCustomers().add(expectedCustomerToRemove);
        expectedAccountEntity.getCustomers().add(expectedCustomerToRetain);
        entityManager.persist(expectedAccountEntity);

        entityManager.getTransaction().commit();

        // Clear the entity manager so the tests are based on persisted data
        entityManager.clear();

        // Find the customers with their associated accounts
        final TypedQuery<CustomerEntity> customerQuery = entityManager
                .createQuery("FROM CustomerEntity c LEFT JOIN FETCH c.accounts WHERE c = :customer",
                        CustomerEntity.class);
        final CustomerEntity actualCustomerToRemove = customerQuery
                .setParameter("customer", expectedCustomerToRemove)
                .getSingleResult();
        final CustomerEntity actualCustomerToRetain = customerQuery
                .setParameter("customer", expectedCustomerToRetain)
                .getSingleResult();

        // Assert that the customers have the expected accounts
        assertTrue(actualCustomerToRemove.getAccounts().contains(expectedAccountEntity));
        assertTrue(actualCustomerToRetain.getAccounts().contains(expectedAccountEntity));

        // AccountEntity 'owns' the relationship so customers must be removed from
        // accounts. Removing accounts from a customer does not result in changes to
        // the database.
        final AccountEntity actualAccount = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.customers WHERE a = :account", AccountEntity.class)
                .setParameter("account", expectedAccountEntity)
                .getSingleResult();
        entityManager.getTransaction().begin();
        actualAccount.getCustomers().remove(actualCustomerToRemove);
        entityManager.getTransaction().commit();

        assertEquals(1, actualAccount.getCustomers().size());
        assertFalse(actualAccount.getCustomers().contains(actualCustomerToRemove));
        assertTrue(actualAccount.getCustomers().contains(expectedCustomerToRetain));
    }

    @Test
    public void testRenameAccount() {
        entityManager.getTransaction().begin();

        // Given a customer exists
        final CustomerEntity customer = getCustomerEntitySupplier().get();
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = getAccountEntitysupplier().get();
        account.getCustomers().add(customer);
        entityManager.persist(account);

        // And the customer is added to the account
        entityManager.getTransaction().commit();
        entityManager.refresh(customer);

        // When the account is renamed
        final String expectedName = String.format("%s (updated)", account.getName());
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE AccountEntity a SET a.name = :name WHERE a = :account")
                .setParameter("account", account)
                .setParameter("name", expectedName)
                .executeUpdate();
        entityManager.getTransaction().commit();

        assertNotEquals(expectedName, account.getName());
        entityManager.refresh(account);
        assertEquals(expectedName, account.getName());

        // Then the customer accounts contains the renamed account
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the renamed account customers contains the customer
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }

    @Test
    public void testAddPaymentCard() {
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        final PaymentCardEntity expectedPaymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();

        entityManager.getTransaction().begin();
        entityManager.persist(accountEntity);
        entityManager.persist(expectedPaymentCardEntity);
        expectedPaymentCardEntity.setAccount(accountEntity);
        accountEntity.getPaymentCards().add(expectedPaymentCardEntity);
        entityManager.getTransaction().commit();

        entityManager.clear();

        final PaymentCardEntity actualPaymentCardEntity = entityManager.find(PaymentCardEntity.class,
                expectedPaymentCardEntity.getId());

        PaymentCardEntities.verifyPaymentCardEntity(expectedPaymentCardEntity, actualPaymentCardEntity);
    }

    @Test
    public void testRemovePaymentCard() {
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        final PaymentCardEntity paymentCardToRetain = getPaymentCardEntitySupplier(accountEntity).get();
        final PaymentCardEntity paymentCardToRemove = getPaymentCardEntitySupplier(accountEntity).get();

        entityManager.getTransaction().begin();
        entityManager.persist(accountEntity);
        entityManager.persist(paymentCardToRetain);
        entityManager.persist(paymentCardToRemove);
        accountEntity.getPaymentCards().add(paymentCardToRetain);
        accountEntity.getPaymentCards().add(paymentCardToRemove);
        entityManager.getTransaction().commit();

        entityManager.clear();

        final PaymentCardEntity actualPaymentCardToRemove = entityManager.find(PaymentCardEntity.class,
                paymentCardToRemove.getId());

        entityManager.getTransaction().begin();
        entityManager.remove(actualPaymentCardToRemove);
        entityManager.getTransaction().commit();

        final AccountEntity persistedAccountEntity = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.paymentCards WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();

        assertEquals(1, persistedAccountEntity.getPaymentCards().size());
        assertTrue(persistedAccountEntity.getPaymentCards().contains(paymentCardToRetain));
        assertFalse(persistedAccountEntity.getPaymentCards().contains(paymentCardToRemove));
    }

    @Test
    public void testAddShoppingBasketItem() {
        // Create an account, a product (book) and an account
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        final BookEntity bookEntity = getBookEntitySupplier().get();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();

        entityManager.getTransaction().begin();
        entityManager.persist(authorEntity);
        entityManager.persist(bookEntity);
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(accountEntity);
        entityManager.getTransaction().commit();

        // Create a shopping basket line item for one book
        final ShoppingBasketItemEntity expectedShoppingBasketItem = new ShoppingBasketItemEntity(
                accountEntity, bookEntity, 1);
        entityManager.getTransaction().begin();
        entityManager.persist(expectedShoppingBasketItem);
        entityManager.getTransaction().commit();

        entityManager.refresh(accountEntity);

        // Verify that the line item is associated with the account
        assertEquals(1, accountEntity.getShoppingBasketItems().size());
        assertTrue(accountEntity.getShoppingBasketItems().contains(expectedShoppingBasketItem));

        entityManager.clear();

        // Read the details of the shopping basket line item from the database
        final ShoppingBasketItemEntity actualShoppingBasketItem = entityManager
                .find(ShoppingBasketItemEntity.class, expectedShoppingBasketItem.getId());

        // Verify the fields
        assertNotSame(expectedShoppingBasketItem, actualShoppingBasketItem);
        ShoppingBasketItemEntities.verifyShoppingBasketItemEntity(expectedShoppingBasketItem,
                actualShoppingBasketItem);
    }

    @Test
    public void testRemoveShoppingBasketItem() {
        // Create an account, some products (books), an account and some shopping basket
        // line items
        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        final BookEntity bookToRetain = getBookEntitySupplier().get();
        final BookEntity bookToRemove = getBookEntitySupplier().get();
        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        final ShoppingBasketItemEntity shoppingBasketItemToRetain = new ShoppingBasketItemEntity(
                accountEntity, bookToRetain, 1);
        final ShoppingBasketItemEntity shoppingBasketItemToRemove = new ShoppingBasketItemEntity(
                accountEntity, bookToRemove, 1);

        entityManager.getTransaction().begin();
        entityManager.persist(authorEntity);
        entityManager.persist(bookToRetain);
        bookToRetain.getAuthors().add(authorEntity);
        entityManager.persist(bookToRemove);
        bookToRemove.getAuthors().add(authorEntity);
        entityManager.persist(accountEntity);
        entityManager.persist(shoppingBasketItemToRetain);
        entityManager.persist(shoppingBasketItemToRemove);
        entityManager.getTransaction().commit();

        entityManager.clear();

        // Find the account and fetch its shopping basket line items
        final AccountEntity actualAccount = entityManager
                .createQuery("FROM AccountEntity a LEFT JOIN FETCH a.shoppingBasketItems WHERE a = :account",
                        AccountEntity.class)
                .setParameter("account", accountEntity)
                .getSingleResult();

        // Read the details of the shopping basket line item from the database
        final ShoppingBasketItemEntity actualShoppingBasketItemToRemove = entityManager
                .find(ShoppingBasketItemEntity.class, shoppingBasketItemToRemove.getId());

        // Remove the line item
        entityManager.getTransaction().begin();
        actualAccount.getShoppingBasketItems().remove(actualShoppingBasketItemToRemove);
        entityManager.getTransaction().commit();

        // Assert that only the expected line items remain
        assertEquals(1, actualAccount.getShoppingBasketItems().size());
        assertTrue(actualAccount.getShoppingBasketItems().contains(shoppingBasketItemToRetain));
        assertFalse(actualAccount.getShoppingBasketItems().contains(shoppingBasketItemToRemove));
    }

    @Test
    public void testDeleteAccount() {
        // Create a fully populated account
        entityManager.getTransaction().begin();

        final AuthorEntity authorEntity = getAuthorEntitysupplier().get();
        entityManager.persist(authorEntity);

        final BookEntity bookEntity = getBookEntitySupplier().get();
        bookEntity.getAuthors().add(authorEntity);
        entityManager.persist(bookEntity);
        authorEntity.getBooks().add(bookEntity);

        final CustomerEntity customerEntity = getCustomerEntitySupplier().get();
        entityManager.persist(customerEntity);

        final AccountEntity accountEntity = getAccountEntitysupplier().get();
        accountEntity.getCustomers().add(customerEntity);
        entityManager.persist(accountEntity);

        final PaymentCardEntity paymentCardEntity = getPaymentCardEntitySupplier(accountEntity).get();
        entityManager.persist(paymentCardEntity);

        final ShoppingBasketItemEntity shoppingBasketItemEntity = new ShoppingBasketItemEntity(
                accountEntity, bookEntity, 1);
        entityManager.persist(shoppingBasketItemEntity);

        entityManager.getTransaction().commit();

        entityManager.clear();

        final AccountEntity persistedAccount = entityManager.find(AccountEntity.class,
                accountEntity.getId());

        entityManager.getTransaction().begin();
        entityManager.remove(persistedAccount);
        entityManager.getTransaction().commit();

        // The deletion of the account should cascade to its payment cards and shopping
        // basket line items.
        assertNull(entityManager.find(AccountEntity.class, accountEntity.getId()));
        assertNull(entityManager.find(PaymentCardEntity.class, paymentCardEntity.getId()));
        assertNull(entityManager.find(ShoppingBasketItemEntity.class, shoppingBasketItemEntity.getId()));
        // The associated products and customers should still exist
        assertNotNull(entityManager.find(BookEntity.class, bookEntity.getId()));
        final CustomerEntity persistedCustomerEntity = entityManager
                .createQuery("FROM CustomerEntity c LEFT JOIN FETCH c.accounts WHERE c = :customer",
                        CustomerEntity.class)
                .setParameter("customer", customerEntity)
                .getSingleResult();
        assertFalse(persistedCustomerEntity.getAccounts().contains(persistedAccount));
    }
}
