package com.github.dtmo.bookshop;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

@Data
@Builder
public final class AuditEvent {
    @NonNull
    private final String action;
    @Singular
    private final Map<String, String> parameters;
    private final String result;
    private final String error;
}
