package com.github.dtmo.bookshop.opensearch;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.bulk.BulkOperation;

import com.github.dtmo.bookshop.gutenberg.PgAuthor;
import com.github.dtmo.bookshop.gutenberg.PgEbook;

public class BookDocumentIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void testIndexBookDocument() throws Exception {
        final BookDocument bookDocument = new BookDocument(1, 1000, "SKUBOOK#1", "Book #1", "Produced by Tess Terr",
                "A test book", Locale.ENGLISH.getLanguage(), null, Set.of("Test Author 1", "Test Author 2"),
                Set.of(1L, 2L));

        getOpenSearchClient().index(indexBuilder -> indexBuilder.index("books")
                .id(String.valueOf(bookDocument.getId()))
                .document(bookDocument));
    }

    @Test
    public void testBulkIndexBookDocuments() throws Exception {
        // Index the Project Gutenberg metadata
        final int bulkOperationBatchSize = 1000;
        final List<BulkOperation> bulkOperations = new ArrayList<>(bulkOperationBatchSize);
        try (final Stream<Path> pathStream = Files.walk(Path.of(System.getenv("HOME"), "rdf-files/top/epub"))) {
            final Iterator<Path> rdfFileIterator = pathStream.filter(path -> !Files.isDirectory(path)).iterator();
            long indexCounter = 0;
            while (rdfFileIterator.hasNext()) {
                final Path rdfFilePath = rdfFileIterator.next();
                try (final InputStream rdfInputStream = Files.newInputStream(rdfFilePath)) {
                    // It seems that a small number of Project Gutenberg's RDF files aren't valid.
                    // It doesn't really matter for our purposes, so we'll just skip over any that
                    // we can't process and index the rest.
                    try {
                        final PgEbook pgEbook = PgEbook.from(rdfInputStream);

                        final BookDocument bookDocument = BookDocument.builder()
                                .id(pgEbook.getEbookNumber())
                                .price(0)
                                .stockKeepingUnit(String.format("PGEBOOK%s", pgEbook.getEbookNumber()))
                                .title(pgEbook.getTitle())
                                .productionCredits(pgEbook.getProductionCredits().orElse(null))
                                .summary(pgEbook.getSummary().orElse(null))
                                .language(pgEbook.getLanguage())
                                .subjects(pgEbook.getSubjects())
                                .authorNames(pgEbook.getAuthors().stream().map(PgAuthor::getName)
                                        .collect(Collectors.toSet()))
                                .authorIds(pgEbook.getAuthors().stream().map(PgAuthor::getAuthorNumber)
                                        .collect(Collectors.toSet()))
                                .build();

                        bulkOperations.add(new BulkOperation.Builder()
                                .index(indexBuilder -> indexBuilder.document(bookDocument))
                                .build());
                        indexCounter++;

                        if (bulkOperations.size() == bulkOperationBatchSize) {
                            getOpenSearchClient().bulk(new BulkRequest.Builder()
                                    .index("books")
                                    .operations(bulkOperations)
                                    .build());
                            System.out.println(String.format("Indexed books: %s", indexCounter));
                            bulkOperations.clear();
                        }
                    } catch (final Exception e) {
                        System.out.println(String.format("Could not process file %s: %s", rdfFilePath, e.getMessage()));
                    }
                }
            }

            if (!bulkOperations.isEmpty()) {
                getOpenSearchClient().bulk(new BulkRequest.Builder()
                        .index("books")
                        .operations(bulkOperations)
                        .build());
                System.out.println(String.format("Indexed books: %s", indexCounter));
            }
        }

        // Wait a little bit for the books to be indexed
        Thread.sleep(Duration.ofSeconds(10));

        // Search for some books
        final SearchResponse<BookDocument> searchResponse = getOpenSearchClient()
                .search(searchRequestBuilder -> searchRequestBuilder
                        .query(queryBuilder -> queryBuilder
                                .match(matchQueryBuilder -> matchQueryBuilder
                                        .field("title")
                                        .query(fieldValueBuilder -> fieldValueBuilder
                                                .stringValue("Moby Dick")))),
                        BookDocument.class);

        System.out.println(String.format("Search hits: %s", searchResponse.hits().hits().size()));
        for (int i = 0; i < searchResponse.hits().hits().size(); i++) {
            System.out.println(searchResponse.hits().hits().get(i).source());
        }
    }
}
