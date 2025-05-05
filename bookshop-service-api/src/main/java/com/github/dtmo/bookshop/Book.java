package com.github.dtmo.bookshop;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public final class Book {
    /**
     * The synthetic ID of the book which needs to be the same value as the
     * corresponding relational database record.
     */
    @JsonProperty("id")
    private final long id;

    /**
     * The price of the book in pennies, cents, etc.
     */
    @JsonProperty("price")
    private final long price;

    /**
     * The unique stock keeping unit (SKU) value of the book.
     */
    @JsonProperty("stock_keeping_unit")
    private final String stockKeepingUnit;

    /**
     * The title of the book, which may not be unique.
     */
    @JsonProperty("title")
    private final String title;

    /**
     * Optional details of how the book was produced. This may be <code>null</code.
     */
    @JsonProperty("production_credits")
    private final String productionCredits;

    /**
     * Optional free text summary of the book. This may be <code>null</code.
     */
    @JsonProperty("summary")
    private final String summary;

    /**
     * The set of languages used in the book, represented as ISO 639 language codes.
     */
    @JsonProperty("languages")
    private final Set<String> languages;

    /**
     * A set of book subject identifiers. This may not be <code>null</code> but may
     * be empty.
     */
    @JsonProperty("subjects")
    private final Set<String> subjects;

    /**
     * The set of author names for the book.
     */
    @JsonProperty("authors")
    private final Set<Author> authors;
}
