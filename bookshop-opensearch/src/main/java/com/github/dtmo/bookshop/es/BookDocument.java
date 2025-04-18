package com.github.dtmo.bookshop.es;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class BookDocument {
    private final long id;

    private final long price;

    private final String stockKeepingUnit;

    private final String title;

    private final String productionCredits;

    private final String summary;

    private final String language;

    private final String subject;

    private final Set<String> authors;
}
