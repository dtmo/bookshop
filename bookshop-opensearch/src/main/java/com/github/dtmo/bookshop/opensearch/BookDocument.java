package com.github.dtmo.bookshop.opensearch;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public final class BookDocument {
    @JsonProperty("id")
    private final long id;

    @JsonProperty("price")
    private final long price;

    @JsonProperty("stock_keeping_unit")
    private final String stockKeepingUnit;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("production_credits")
    private final String productionCredits;

    @JsonProperty("summary")
    private final String summary;

    @JsonProperty("language")
    private final String language;

    @JsonProperty("subjects")
    private final Set<String> subjects;

    @JsonProperty("authors")
    private final Set<String> authors;
}
