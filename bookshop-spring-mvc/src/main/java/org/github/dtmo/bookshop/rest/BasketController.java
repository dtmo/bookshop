package org.github.dtmo.bookshop.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.github.dtmo.bookshop.BookshopService;
import com.github.dtmo.bookshop.ShoppingBasketItem;
import com.github.dtmo.bookshop.rest.BasketApi;

public class BasketController implements BasketApi {

    @Autowired
    private BookshopService bookshopService;

    @Override
    public ResponseEntity<List<ShoppingBasketItem>> basketPut(final Long accountId, final Long bookId, final Long quantity) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getPrincipal();
        final List<ShoppingBasketItem> shoppingBasketItems = List.of();
        return ResponseEntity.ok().body(shoppingBasketItems);
    }

}
