package com.github.dtmo.bookshop;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ShoppingBasketItem {
    @JsonProperty("account_id")
    private long accountId;

    @JsonProperty("book_id")
    private long bookId;

    @JsonProperty("quantity")
    private long quantity;
}
