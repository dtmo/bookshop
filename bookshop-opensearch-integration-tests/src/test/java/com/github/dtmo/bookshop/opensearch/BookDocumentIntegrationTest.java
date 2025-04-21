package com.github.dtmo.bookshop.opensearch;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;

public class BookDocumentIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void testIndexBookDocument() throws Exception {
        final BookDocument bookDocument = new BookDocument(1, 1000, "SKUBOOK#1", "Book #1", "Produced by Tess Terr",
                "A test book", Locale.ENGLISH.getLanguage(), null, Set.of("Test Author 1", "Test Author 2"));

        getOpenSearchClient().index(indexBuilder -> indexBuilder.index("books")
                .id(String.valueOf(bookDocument.getId()))
                .document(bookDocument));
    }
}
