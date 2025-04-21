package com.github.dtmo.bookshop.gutenberg;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.jena.rdf.model.Resource;

public class PgAuthor {
    private final Resource authorResource;

    public PgAuthor(final Resource authorResource) {
        this.authorResource = authorResource;
    }

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

    public String getName() {
        return authorResource.getProperty(PGTerms.name).getString();
    }

    public String getAlias() {
        return authorResource.getProperty(PGTerms.alias).getString();
    }

    public int getBirthdate() {
        return authorResource.getProperty(PGTerms.birthdate).getInt();
    }

    public int getDeathdate() {
        return authorResource.getProperty(PGTerms.deathdate).getInt();
    }

    public String getWebpage() {
        return authorResource.getProperty(PGTerms.webpage).getString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final long authorNumber = getAuthorNumber();
        result = prime * result + (int) (authorNumber ^ (authorNumber >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PgAuthor other = (PgAuthor) obj;
        if (getAuthorNumber() != other.getAuthorNumber())
            return false;
        return true;
    }
}
