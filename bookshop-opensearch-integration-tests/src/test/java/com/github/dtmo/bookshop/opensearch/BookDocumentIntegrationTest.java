package com.github.dtmo.bookshop.opensearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.search.Hit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BookDocumentIntegrationTest extends AbstractIntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testIndexBookDocument() throws Exception {
        final BookDocument bookDocument = new BookDocument(1, 1000, "SKUBOOK#1", "Book #1", "Produced by Tess Terr",
                "A test book", Locale.ENGLISH.getLanguage(), null, Set.of("Test Author 1", "Test Author 2"),
                Set.of(1L, 2L));

        getOpenSearchClient().index(indexBuilder -> indexBuilder.index("books")
                .id(String.valueOf(bookDocument.getId()))
                .document(bookDocument));
    }

    /**
     * @throws Exception
     */
    @Test
    public void testBulkIndexBookDocuments() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Load the prepared dataset of 100 BookDocument objects
        final List<BookDocument> bookDocuments;
        try (final InputStream inputStream = BookDocumentIntegrationTest.class
                .getResourceAsStream("gutenberg_top_100_book_documents.json")) {
            bookDocuments = objectMapper.readValue(inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BookDocument.class));
        }

        // Index the BookDocuments in a batch operation
        openSearchClient.bulk(new BulkRequest.Builder()
                .index(BOOKS_INDEX_NAME)
                .operations(bookDocuments.stream()
                        .map(bookDocument -> new BulkOperation.Builder()
                                .index(indexBuilder -> indexBuilder.document(bookDocument))
                                .build())
                        .collect(Collectors.toList()))
                .build());

        // Wait for the books to be indexed
        while (openSearchClient.indices()
                .stats(indicesStatsRequestBuilder -> indicesStatsRequestBuilder.index(BOOKS_INDEX_NAME))
                .indices()
                .get(BOOKS_INDEX_NAME)
                .primaries()
                .docs()
                .count() < bookDocuments.size()) {
            Thread.sleep(Duration.ofMillis(500));
        }

        // Search for some books
        final SearchResponse<BookDocument> searchResponse = openSearchClient
                .search(searchRequestBuilder -> searchRequestBuilder
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("title")
                                        .query(fieldValueBuilder -> fieldValueBuilder
                                                .stringValue("Moby Dick")))),
                        BookDocument.class);

        // Three books contain the word "Moby":
        // "Moby Dick; Or, The Whale",
        // "Moby Word Lists", and
        // "Moby Multiple Language Lists of Common Words"

        final List<Hit<BookDocument>> hits = searchResponse.hits().hits();
        assertEquals(3, hits.size());

        final List<BookDocument> bookDocumentHits = hits.stream().map(Hit::source).collect(Collectors.toList());
        assertTrue(bookDocumentHits.stream()
                .anyMatch(bookDocument -> "Moby Dick; Or, The Whale".equals(bookDocument.getTitle())));
        assertTrue(bookDocumentHits.stream()
                .anyMatch(bookDocument -> "Moby Word Lists".equals(bookDocument.getTitle())));
        assertTrue(bookDocumentHits.stream()
                .anyMatch(bookDocument -> "Moby Multiple Language Lists of Common Words"
                        .equals(bookDocument.getTitle())));
    }
}
