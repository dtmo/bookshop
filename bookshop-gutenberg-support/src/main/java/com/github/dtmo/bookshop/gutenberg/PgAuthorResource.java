package com.github.dtmo.bookshop.gutenberg;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import lombok.Data;

/**
 * PgAuthorResource provides convenient methods to access information from an
 * RDF representation of a <a href="https://www.gutenberg.org/">Project
 * Gutenberg</a> author.
 */
@Data
public final class PgAuthorResource {
    private final Resource authorResource;

    /**
     * Constructs a new instance of PgAuthorResource from an RDF resource.
     * 
     * @param authorResource The RDF resource representing the author.
     */
    public PgAuthorResource(final Resource authorResource) {
        this.authorResource = authorResource;
    }

    /**
     * @return The unique ID of the author.
     */
    public long getAuthorNumber() {
        try {
            final URI resourceUri = new URI(authorResource.getURI());
            final Path resourceUriPath = Path.of(resourceUri.getPath());
            final long authorNumber = Long
                    .parseLong(resourceUriPath.getName(resourceUriPath.getNameCount() - 1).toString());

            return authorNumber;
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Could not parse author resource URI: " + authorResource.getURI(), e);
        }
    }

    /**
     * @return The author's name.
     */
    public String getName() {
        return authorResource.getProperty(PGTerms.name).getString();
    }

    /**
     * @return Aternative representations of the author's name.
     */
    public Set<String> getAliases() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(authorResource.listProperties(PGTerms.alias),
                        Spliterator.ORDERED), false)
                .map(Statement::getString)
                .collect(Collectors.toSet());
    }

    /**
     * @return The year that the author was born.
     */
    public int getBirthdate() {
        return authorResource.getProperty(PGTerms.birthdate).getInt();
    }

    /**
     * @return The year that the author died.
     */
    public int getDeathdate() {
        return authorResource.getProperty(PGTerms.deathdate).getInt();
    }

    /**
     * @return The URL of a web page about the author.
     */
    public String getWebpage() {
        return authorResource.getPropertyResourceValue(PGTerms.webpage).getURI();
    }
}
