package com.github.dtmo.bookshop.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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
    public void testSettingCustomerAccounts() {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // Given a customer exists
        final CustomerEntity customer = new CustomerEntity("John Segundus");
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = new AccountEntity("The Learned Society of York Magicians");
        entityManager.persist(account);

        entityTransaction.commit();

        // And the customer and account are not linked
        assertNull(customer.getAccounts());
        assertNull(account.getCustomers());

        // When the account is associated with the customer
        entityTransaction.begin();
        customer.setAccounts(Set.of(account));
        entityTransaction.commit();

        // Then the customer accounts includes the account
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the account customers includes the customer
        entityManager.refresh(account);
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }

    @Test
    public void testRenamingAnAccount() {
        final EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        // Given a customer exists
        final CustomerEntity customer = new CustomerEntity("John Segundus");
        entityManager.persist(customer);

        // And an account exists
        final AccountEntity account = new AccountEntity("The Learned Society of York Magicians");
        entityManager.persist(account);

        // And the customer and account are linked
        customer.setAccounts(Set.of(account));

        entityTransaction.commit();

        // When the account is renamed
        entityTransaction.begin();
        // (Mr. Norrell made the statues in York Cathedral speak)
        account.setName("The Learned Society of York Magicians (disbanded)");
        entityTransaction.commit();

        // Then the customer accounts contains the renamed account
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));

        // And the renamed account customers contains the customer
        entityManager.refresh(account);
        assertEquals(1, account.getCustomers().size());
        assertTrue(account.getCustomers().contains(customer));
    }
}
