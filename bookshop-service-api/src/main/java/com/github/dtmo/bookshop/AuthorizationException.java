package com.github.dtmo.bookshop;

/**
 * AuthorizationException is thrown when a service request fails authorization.
 */
public class AuthorizationException extends RuntimeException {
    static final long serialVersionUID = 1L;

    public AuthorizationException(final String message) {
        super(message);
    }
}
