package com.github.dtmo.bookshop.opensearch;

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
        final Network network = Network.newNetwork();

        // Initialise the OpenSearch container
        final OpensearchContainer<?> opensearchContainer = new OpensearchContainer<>(
                DockerImageName.parse("opensearchproject/opensearch:1"))
                .withNetwork(network);
        opensearchContainer.start();

        // Connect to the container so that there will be an instance of
        // OpenSearchClient for tests to use.
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
    }

    /**
     * Returns a connected OpenSearchClient instance for tests to use to interact
     * with the Opensearch service.
     * 
     * @return A shared instance of OpenSearchClient.
     */
    protected static OpenSearchClient getOpenSearchClient() {
        return openSearchClient;
    }
}
