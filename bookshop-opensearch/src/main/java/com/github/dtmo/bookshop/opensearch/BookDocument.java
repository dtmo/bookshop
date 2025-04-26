package com.github.dtmo.bookshop.opensearch;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * BookDocument is a representation of a book for storage in a search engine
 * such as OpenSearch.
 */
@Data
@Builder
@Jacksonized
public final class BookDocument {
    public static final String ID_FIELD = "id";
    public static final String PRICE_FIELD = "price";
    public static final String STOCK_KEEPING_UNIT_FIELD = "stock_keeping_unit";
    public static final String TITLE_FIELD = "title";
    public static final String PRODUCTION_CREDITS_FIELD = "production_credits";
    public static final String SUMMARY_FIELD = "summary";
    public static final String LANGUAGE_FIELD = "language";
    public static final String SUBJECTS_FIELD = "subjects";
    public static final String AUTHOR_NAMES_FIELD = "author_names";
    public static final String AUTHOR_IDS_FIELD = "author_ids";

    /**
     * The synthetic ID of the book which needs to be the same value as the
     * corresponding relational database record.
     */
    @JsonProperty(ID_FIELD)
    private final long id;

    /**
     * The price of the book in pennies, cents, etc.
     */
    @JsonProperty(PRICE_FIELD)
    private final long price;

    /**
     * The unique stock keeping unit (SKU) value of the book.
     */
    @JsonProperty(STOCK_KEEPING_UNIT_FIELD)
    private final String stockKeepingUnit;

    /**
     * The title of the book, which may not be unique.
     */
    @JsonProperty(TITLE_FIELD)
    private final String title;

    /**
     * Optional details of how the book was produced. This may be <code>null</code.
     */
    @JsonProperty(PRODUCTION_CREDITS_FIELD)
    private final String productionCredits;

    /**
     * Optional free text summary of the book. This may be <code>null</code.
     */
    @JsonProperty(SUMMARY_FIELD)
    private final String summary;

    /**
     * Optional book language represented as an ISO 639 language code. This may be
     * <code>null</code.
     */
    @JsonProperty(LANGUAGE_FIELD)
    private final String language;

    /**
     * A set of book subject identifiers. This may not be <code>null</code> but may
     * be empty.
     */
    @JsonProperty(SUBJECTS_FIELD)
    private final Set<String> subjects;

    /**
     * The set of author names for the book.
     */
    @JsonProperty(AUTHOR_NAMES_FIELD)
    private final Set<String> authorNames;

    /**
     * The set of author IDs for the book. These IDs must match the corresponding
     * relational database entries for the authors.
     */
    @JsonProperty(AUTHOR_IDS_FIELD)
    private final Set<Long> authorIds;
}
