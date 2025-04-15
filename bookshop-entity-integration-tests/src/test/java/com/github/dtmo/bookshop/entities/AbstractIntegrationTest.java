package com.github.dtmo.bookshop.entities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.utility.DockerImageName;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceConfiguration;

/**
 * AbstractIntegrationTest provides reusable environment setup code so that any
 * tests wanting to use the Testcontainers PostgreSQL bookshop database schema
 * will not have to wait for the container to repeatedly stop and start.
 */
public abstract class AbstractIntegrationTest {
    private static final Supplier<AccountEntity> accountEntitySupplier = AccountEntities
            .createIncrementingNameSupplier();

    private static final Supplier<AuthorEntity> authorEntitySupplier = AuthorEntities
            .createIncrementingAuthorNameSupplier();

    private static final Supplier<BookEntity> bookEntitySupplier = BookEntities.createIncrementingBookSupplier();

    private static final Supplier<CustomerEntity> customerEntitySupplier = CustomerEntities
            .createIncrementingNameSupplier();

    private static final Map<AccountEntity, Supplier<PaymentCardEntity>> accountPaymentCardSuppliers = new HashMap<>();

    private static EntityManagerFactory entityManagerFactory;

    // The use of this static initializer to create the containers and entity
    // manager factory is based on the "Testcontainers container lifecycle
    // management using JUnit 5" guide section on using singleton containers.
    // https://testcontainers.com/guides/testcontainers-container-lifecycle/#_using_singleton_containers
    static {
        // Create a network so that the PostgreSQL and Liquibase containers can
        // easily talk to each other.
        final Network network = Network.newNetwork();

        // PostgreSQL is going to be our relational database, but will need to
        // have the bookshop database schema installed before it can be useful.
        final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:17"))
                .withNetwork(network)
                .withDatabaseName("bookshop_db")
                .withUsername("bookshop_user")
                .withPassword("bookshop_password");
        postgresContainer.start();

        // We will query the running PostgreSQL container to get configuration
        // value for Liquibase.
        // The PostgreSQL and Liquibase containers are both attached to the same
        // network, so can communicate without worrying about exposed ports.
        // The PostgreSQL container gets a network alias that we can use as the
        // address for Liquibase to connect to.
        final String url = String.format("jdbc:postgresql://%s:%s/%s", postgresContainer.getNetworkAliases().get(0),
                PostgreSQLContainer.POSTGRESQL_PORT, postgresContainer.getDatabaseName());
        final String changeLogFile = "changelog.xml";

        // The Liquibase container is a one-shot container that will start,
        // install the bookshop schema, and then naturally stop again.
        // We state that it depends on the PostgreSQL container to make sure it
        // doesn't start too soon.
        final GenericContainer<?> liquibaseContainer = new GenericContainer<>(DockerImageName.parse("liquibase:latest"))
                .withNetwork(network)
                .withWorkingDirectory("/liquibase/changelog")
                .withClasspathResourceMapping("/liquibase/changelog", "/liquibase/changelog",
                        BindMode.READ_ONLY)
                .withCommand(String.format(
                        "liquibase update --driver=%s --url=%s --changeLogFile=%s --username=%s --password=%s",
                        postgresContainer.getDriverClassName(), url, changeLogFile,
                        postgresContainer.getUsername(),
                        postgresContainer.getPassword()))
                .withStartupCheckStrategy(
                        new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(30)))
                .dependsOn(postgresContainer);

        // The Liquibase container startup check strategy should cause the start
        // method to block unitil the container has exited. This means that we
        // will avoid trying to create an EntityManagerFactory before the schema
        // has been loaded.
        liquibaseContainer.start();

        // We can query the PostgreSQL container for the details that JPA needs
        // to be able to connect to the database. We dynamically configure the
        // persistence unit properties here, and finally create the entity
        // manager factory.
        Map<String, String> persistenceUnitProperties = new HashMap<>();
        persistenceUnitProperties.put(PersistenceConfiguration.JDBC_DRIVER, postgresContainer.getDriverClassName());
        persistenceUnitProperties.put(PersistenceConfiguration.JDBC_URL, postgresContainer.getJdbcUrl());
        persistenceUnitProperties.put(PersistenceConfiguration.JDBC_USER, postgresContainer.getUsername());
        persistenceUnitProperties.put(PersistenceConfiguration.JDBC_PASSWORD, postgresContainer.getPassword());

        persistenceUnitProperties.put("hibernate.show_sql", "true");
        persistenceUnitProperties.put("hibernate.format_sql", "true");
        persistenceUnitProperties.put("hibernate.highlight_sql", "true");

        entityManagerFactory = Persistence.createEntityManagerFactory("com.github.dtmo.bookshop.entities",
                persistenceUnitProperties);
    }

    protected static Supplier<AccountEntity> getAccountEntitysupplier() {
        return accountEntitySupplier;
    }

    protected static Supplier<AuthorEntity> getAuthorEntitysupplier() {
        return authorEntitySupplier;
    }

    protected static Supplier<BookEntity> getBookEntitySupplier() {
        return bookEntitySupplier;
    }

    protected static Supplier<CustomerEntity> getCustomerEntitySupplier() {
        return customerEntitySupplier;
    }

    protected static Supplier<PaymentCardEntity> getPaymentCardEntitySupplier(final AccountEntity accountEntity) {
        return accountPaymentCardSuppliers.computeIfAbsent(accountEntity,
                PaymentCardEntities::createIncrementingPaymentCardSupplier);
    }

    /**
     * @return The entity manager factory to use to communicate with the database.
     */
    protected static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
