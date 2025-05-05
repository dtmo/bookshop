package com.github.dtmo.bookshop;

public interface AuditLogger {
    void logAuditEvent(final User principal, final AuditEvent auditEvent);
}
