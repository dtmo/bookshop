package com.github.dtmo.bookshop.entities;

import static org.junit.Assert.assertNotSame;
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
import jakarta.persistence.EntityTransaction;
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
    public void testCreateAccount() {
        final AccountEntity expectedAccountEntity = getAccountEntitysupplier().get();

        // Assert that the account does not exist in the database
        final TypedQuery<AccountEntity> accountQuery = entityManager
                .createQuery("FROM AccountEntity a WHERE a.name = :name", AccountEntity.class)
                .setParameter("name", expectedAccountEntity.getName());
        assertTrue(accountQuery.getResultList().isEmpty());

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
    public void testAddingCustomersToAccounts() {
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
    public void testRemovingCustomersFromAccounts() {
        // Create an account and two customers
        final AccountEntity expectedAccountEntity = getAccountEntitysupplier().get();
        final CustomerEntity expectedCustomerToRemove = getCustomerEntitySupplier().get();
        final CustomerEntity expectedCustomerToRetain = getCustomerEntitySupplier().get();

        entityManager.getTransaction().begin();
        entityManager.persist(expectedAccountEntity);
        entityManager.persist(expectedCustomerToRemove);
        entityManager.persist(expectedCustomerToRetain);
        entityManager.getTransaction().commit();

        // Add the account to both customers
        entityManager.getTransaction().begin();
        expectedAccountEntity.getCustomers().add(expectedCustomerToRemove);
        expectedAccountEntity.getCustomers().add(expectedCustomerToRetain);
        entityManager.getTransaction().commit();

        // Clear the entity manager so the tests are based on persisted data
        entityManager.clear();

        // Find the customers with their associated accounts
        final TypedQuery<CustomerEntity> customerQuery = entityManager
                .createQuery("FROM CustomerEntity c JOIN FETCH c.accounts WHERE c = :customer", CustomerEntity.class);
        final CustomerEntity actualCustomerToRemove = customerQuery
                .setParameter("customer", expectedCustomerToRemove).getSingleResult();
        final CustomerEntity actualCustomerToRetain = customerQuery
                .setParameter("customer", expectedCustomerToRetain).getSingleResult();

        // Assert that the customers have the expected accounts
        assertTrue(actualCustomerToRemove.getAccounts().contains(expectedAccountEntity));
        assertTrue(actualCustomerToRetain.getAccounts().contains(expectedAccountEntity));

        // AccountEntity 'owns' the relationship so customers must be removed from
        // accounts. Removing accounts from a customer does not result in changes to
        // the database.
        final AccountEntity actualAccount = entityManager
                .createQuery("FROM AccountEntity a JOIN FETCH a.customers WHERE a = :account", AccountEntity.class)
                .setParameter("account", expectedAccountEntity).getSingleResult();
        entityManager.getTransaction().begin();
        actualAccount.getCustomers().remove(actualCustomerToRemove);
        entityManager.getTransaction().commit();

        assertEquals(1, actualAccount.getCustomers().size());
        assertFalse(actualAccount.getCustomers().contains(actualCustomerToRemove));
        assertTrue(actualAccount.getCustomers().contains(expectedCustomerToRetain));
    }

    @Test
    public void testRenamingAnAccount() {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // Given a customer exists
        final CustomerEntity customer = getCustomerEntitySupplier().get();
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = getAccountEntitysupplier().get();
        entityManager.persist(account);

        // And the customer is added to the account
        account.getCustomers().add(customer);
        entityTransaction.commit();

        // When the account is renamed
        entityTransaction.begin();
        account.setName(String.format("%s (updated)", account.getName()));
        entityTransaction.commit();

        // Then the customer accounts contains the renamed account
        entityManager.refresh(customer);
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the renamed account customers contains the customer
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }
}
