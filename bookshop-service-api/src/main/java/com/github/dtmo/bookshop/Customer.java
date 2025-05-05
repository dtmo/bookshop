package com.github.dtmo.bookshop;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public final class Customer {
    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;
}
