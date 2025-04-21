package com.github.dtmo.bookshop.opensearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.testcontainers.OpensearchContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractIntegrationTest {
    private static final OpenSearchClient openSearchClient;

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
        final OpensearchContainer<?> opensearchContainer = new OpensearchContainer<>(
                DockerImageName.parse("opensearchproject/opensearch:1"))
                .withNetwork(network);
        opensearchContainer.start();

        final HttpHost httpHost = HttpHost.create(opensearchContainer.getHttpHostAddress());
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(httpHost),
                new UsernamePasswordCredentials(opensearchContainer.getUsername(), opensearchContainer.getPassword()));
        final RestClient restClient = RestClient.builder(httpHost)
                .setHttpClientConfigCallback(
                        httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
                .build();
        final OpenSearchTransport openSearchTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        openSearchClient = new OpenSearchClient(openSearchTransport);

        try {
            openSearchClient.indices().create(
                    indexBuilder -> indexBuilder.index("books")
                            .mappings(mappingBuilder -> mappingBuilder
                                    .withJson(BookDocument.class.getResourceAsStream("books.json"))));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize Elasticsearch", e);
        }
    }

    protected OpenSearchClient getOpenSearchClient() {
        return openSearchClient;
    }
}
