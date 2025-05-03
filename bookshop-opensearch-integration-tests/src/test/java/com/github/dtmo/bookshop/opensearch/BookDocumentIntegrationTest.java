package com.github.dtmo.bookshop.opensearch;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.ExistsQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;
import org.opensearch.client.opensearch.core.search.Hit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BookDocumentIntegrationTest extends AbstractIntegrationTest {
    private static final String gutenberg_top_100_books_index = "gutenberg_top_100_books";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void beforeAll() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Define index mapping
        openSearchClient.indices().create(
                indexBuilder -> indexBuilder.index(gutenberg_top_100_books_index)
                        .mappings(mappingBuilder -> mappingBuilder
                                .withJson(BookDocument.class.getResourceAsStream("books.json"))));

        // Load the prepared dataset of 100 BookDocument objects
        final List<BookDocument> bookDocuments;
        try (final InputStream inputStream = BookDocumentIntegrationTest.class
                .getResourceAsStream("gutenberg_top_100_book_documents.json")) {
            bookDocuments = objectMapper.readValue(inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BookDocument.class));
        }

        assertEquals(100, bookDocuments.size());

        // Index the BookDocuments in a batch operation
        openSearchClient.bulk(new BulkRequest.Builder()
                .index(gutenberg_top_100_books_index)
                .operations(bookDocuments.stream()
                        .map(bookDocument -> new BulkOperation.Builder()
                                .index(indexBuilder -> indexBuilder.document(bookDocument))
                                .build())
                        .collect(Collectors.toList()))
                .build());

        // Wait for the books to be indexed
        final Duration limit = Duration.ofSeconds(10);
        final Instant timeout = Instant.now().plus(limit);
        while (Instant.now().isBefore(timeout) && openSearchClient.indices()
                .stats(indicesStatsRequestBuilder -> indicesStatsRequestBuilder.index(gutenberg_top_100_books_index))
                .indices()
                .get(gutenberg_top_100_books_index)
                .primaries()
                .docs()
                .count() < bookDocuments.size()) {
            Thread.sleep(Duration.ofMillis(500));
        }
    }

    @Test
    public void testBulkIndexBookDocuments() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        final long actualIndexedDocumentCount = openSearchClient.indices()
                .stats(indicesStatsRequestBuilder -> indicesStatsRequestBuilder.index(gutenberg_top_100_books_index))
                .indices()
                .get(gutenberg_top_100_books_index)
                .primaries()
                .docs()
                .count();
        assertEquals(100, actualIndexedDocumentCount);
    }

    @Test
    public void testQueryByTitle() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Search for books by title
        final SearchResponse<BookDocument> searchResponse = openSearchClient
                .search(searchRequestBuilder -> searchRequestBuilder
                        .index(gutenberg_top_100_books_index)
                        .size(100)
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field(BookDocument.TITLE_FIELD)
                                        .query(fieldValueBuilder -> fieldValueBuilder
                                                .stringValue("Moby Dick"))
                                        .fuzziness("AUTO"))),
                        BookDocument.class);

        // Three books contain the word "Moby":
        // - "Moby Dick; Or, The Whale",
        // - "Moby Word Lists", and
        // - "Moby Multiple Language Lists of Common Words"
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

    @Test
    public void testQueryByPriceRange() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Search for books by price
        final SearchResponse<BookDocument> searchResponse = openSearchClient.search(
                searchRequestBuilder -> searchRequestBuilder
                        .index(gutenberg_top_100_books_index)
                        .size(100)
                        .query(queryBuilder -> queryBuilder
                                .range(rangeQueryBuilder -> rangeQueryBuilder
                                        .field(BookDocument.PRICE_FIELD)
                                        .gte(JsonData.of(100))
                                        .lte(JsonData.of(5000)))),
                BookDocument.class);

        // Each book price increments by 100 from a starting price of 100
        // We are therefore expecting 50 hits.
        final List<Hit<BookDocument>> hits = searchResponse.hits().hits();
        assertEquals(50, hits.size());
    }

    @Test
    public void testQueryByLanguageOtherThanEnglish() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Search for books with a language other than English
        final SearchResponse<BookDocument> searchResponse = openSearchClient.search(
                new SearchRequest.Builder()
                        .index(gutenberg_top_100_books_index)
                        .size(100)
                        .query(new Query.Builder()
                                .bool(new BoolQuery.Builder()
                                        .must(new Query.Builder()
                                                .exists(new ExistsQuery.Builder()
                                                        .field(BookDocument.LANGUAGES_FIELD)
                                                        .build())
                                                .build())
                                        .mustNot(
                                                new Query.Builder()
                                                        .term(new TermQuery.Builder()
                                                                .field(BookDocument.LANGUAGES_FIELD)
                                                                .value(new FieldValue.Builder()
                                                                        .stringValue(Locale.ENGLISH.getLanguage())
                                                                        .build())
                                                                .build())
                                                        .build())
                                        .build())
                                .build())
                        .build(),
                BookDocument.class);

        // There are 6 books with a language other tham English in the top 100.
        final List<Hit<BookDocument>> hits = searchResponse.hits().hits();
        assertEquals(6, hits.size());

        final Iterator<Hit<BookDocument>> hitIterator = hits.iterator();
        while (hitIterator.hasNext()) {
            final BookDocument nonEnglishBook = hitIterator.next().source();

            assertNotEquals(Locale.ENGLISH.getLanguage(), nonEnglishBook);
        }
    }

    @Test
    public void testQueryBySubject() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Search for books with a language other than English
        final SearchResponse<BookDocument> searchResponse = openSearchClient.search(
                new SearchRequest.Builder()
                        .index(gutenberg_top_100_books_index)
                        .size(100)
                        .query(new Query.Builder()
                                .match(new MatchQuery.Builder()
                                        .field(BookDocument.SUBJECTS_FIELD)
                                        .query(new FieldValue.Builder()
                                                .stringValue("humour")
                                                .build())
                                        .fuzziness("AUTO")
                                        .build())
                                .build())
                        .build(),
                BookDocument.class);

        // Six books include references to humour in their subjects:
        // - Emma
        // - The Adventures of Tom Sawyer, Complete
        // - Adventures of Huckleberry Finn
        // - A Room with a View
        // - History of Tom Jones, a Foundling
        // - A Modest Proposal
        final List<Hit<BookDocument>> hits = searchResponse.hits().hits();
        assertEquals(6, hits.size());
    }

    @Test
    public void testQueryByAuthorId() throws Exception {
        final OpenSearchClient openSearchClient = getOpenSearchClient();

        // Search for books with an author_id of 68 (Jane Austen)
        final SearchResponse<BookDocument> searchResponse = openSearchClient.search(
                new SearchRequest.Builder()
                        .index(gutenberg_top_100_books_index)
                        .size(100)
                        .query(new Query.Builder()
                                .term(new TermQuery.Builder()
                                        .field(BookDocument.AUTHOR_IDS_FIELD)
                                        .value(new FieldValue.Builder()
                                                .longValue(68)
                                                .build())
                                        .build())
                                .build())
                        .build(),
                BookDocument.class);

        // Jane Austen authored two books in the Project Gutenberg top 100:
        // - Pride and Prejudice
        // - Emma
        final List<Hit<BookDocument>> hits = searchResponse.hits().hits();
        assertEquals(2, hits.size());

        final Iterator<Hit<BookDocument>> hitIterator = hits.iterator();
        while (hitIterator.hasNext()) {
            final BookDocument janeAustenBook = hitIterator.next().source();

            assertTrue(janeAustenBook.getAuthorIds().contains(Long.valueOf(68)));
        }
    }
}
