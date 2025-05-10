package com.github.dtmo.bookshop;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ShoppingBasketItem {

    @JsonProperty("book")
    private Book book;

    @JsonProperty("quantity")
    private long quantity;
}
