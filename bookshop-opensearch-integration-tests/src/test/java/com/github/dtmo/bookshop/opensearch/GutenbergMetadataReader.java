package com.github.dtmo.bookshop.opensearch;

import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.opensearch.client.json.jackson.JacksonJsonpGenerator;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.core.BulkRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

public class GutenbergMetadataReader {
    private static final JsonFactory jsonFactory = new JsonFactory();
    private static final JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper();
    private static final CsvMapper csvMapper = CsvMapper.builder().addModule(new JavaTimeModule()).build();

    @Data
    @Builder
    @Jacksonized
    public static final class PgCatalogText {
        @JsonProperty("Text#")
        private final long textNum;

        @JsonProperty("Type")
        private final String type;

        @JsonProperty("Issued")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
        private final Date issued;

        @JsonProperty("Title")
        private final String title;

        @JsonProperty("Language")
        private final String language;

        @JsonProperty("Authors")
        private final List<String> authors;

        @JsonProperty("Subjects")
        private final List<String> subjects;

        @JsonProperty("LoCC")
        private final List<String> libraryOfCongressClassifications;

        @JsonProperty("Bookshelves")
        private final List<String> bookshelves;
    }

    public static void main(final String[] args) throws Exception {
        final CsvSchema csvSchema = CsvSchema.builder()
                .addColumn("Text#")
                .addColumn("Type")
                .addColumn("Issued")
                .addColumn("Title")
                .addColumn("Language")
                .addArrayColumn("Authors", "; ")
                .addArrayColumn("Subjects", "; ")
                .addArrayColumn("LoCC", "; ")
                .addArrayColumn("Bookshelves", "; ")
                .build();

        final MappingIterator<PgCatalogText> mappingIterator = csvMapper.readerFor(PgCatalogText.class)
                .with(csvSchema.withHeader())
                .readValues(Paths.get("/home/dtmor/git/dtmo/bookshop/pg_catalog.csv").toFile());

        final BulkRequest.Builder bulkRequestBuiler = new BulkRequest.Builder();
        StreamSupport.stream(Spliterators.spliteratorUnknownSize(mappingIterator, Spliterator.ORDERED), false)
                // .limit(10)
                .forEach(pgCatalogText -> bulkRequestBuiler
                        .operations(bulkOperationBuilder -> bulkOperationBuilder
                                .index(indexOperationBuilder -> indexOperationBuilder
                                        .index("books")
                                        .document(BookDocument.builder()
                                                .id(pgCatalogText.getTextNum())
                                                .price(0)
                                                .stockKeepingUnit(
                                                        String.format("PGBOOK#%s", pgCatalogText.getTextNum()))
                                                .title(pgCatalogText.getTitle())
                                                .productionCredits(null)
                                                .summary(null)
                                                .language(pgCatalogText.getLanguage())
                                                .subjects(Set.copyOf(pgCatalogText.getSubjects()))
                                                .authors(Set.copyOf(pgCatalogText.getAuthors()))
                                                .build()))));

        final BulkRequest bulkRequest = bulkRequestBuiler.build();

        final StringWriter stringWriter = new StringWriter();
        final JacksonJsonpGenerator jsonGenerator = new JacksonJsonpGenerator(
                jsonFactory.createGenerator(stringWriter));

        bulkRequest.serialize(jsonGenerator, jsonpMapper);
        jsonGenerator.close();

        System.out.println(stringWriter.toString());
    }
}
