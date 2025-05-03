package com.github.dtmo.bookshop.gutenberg;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

public class PgEbook {
    private final Resource ebookResource;

    public PgEbook(final Resource ebookResource) {
        this.ebookResource = ebookResource;
    }

    public long getEbookNumber() {
        try {
            final URI resourceUri = new URI(ebookResource.getURI());
            final Path resourceUriPath = Path.of(resourceUri.getPath());
            final long ebookNumber = Long
                    .parseLong(resourceUriPath.getName(resourceUriPath.getNameCount() - 1).toString());

            return ebookNumber;
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Could not parse ebook resource URI: " + ebookResource.getURI(), e);
        }
    }

    public String getTitle() {
        return ebookResource.getProperty(DCTerms.title).getString();
    }

    public Optional<String> getProductionCredits() {
        final String productionCredits;
        if (ebookResource.hasProperty(PGTerms.marc508)) {
            productionCredits = ebookResource.getProperty(PGTerms.marc508).getString()
                    .lines()
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining(" "));
        } else {
            productionCredits = null;
        }
        return Optional.ofNullable(productionCredits);
    }

    public Optional<String> getSummary() {
        final String summary;
        if (ebookResource.hasProperty(PGTerms.marc520)) {
            summary = ebookResource.getProperty(PGTerms.marc520).getString().lines()
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining(" "));
        } else {
            summary = null;
        }
        return Optional.ofNullable(summary);
    }

    public String getLanguage() {
        return ebookResource.getProperty(DCTerms.language).getObject().asResource()
                .getProperty(RDF.value).getString();
    }

    public Set<String> getSubjects() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(ebookResource.listProperties(DCTerms.subject),
                        Spliterator.ORDERED), false)
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(propertyResource -> propertyResource.getProperty(RDF.value).getString())
                .collect(Collectors.toSet());
    }

    public Set<PgAuthor> getAuthors() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(ebookResource.listProperties(DCTerms.creator),
                        Spliterator.ORDERED), false)
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(PgAuthor::new)
                .collect(Collectors.toSet());
    }

    public static PgEbook from(final InputStream inputStream) {
        final Model model = RDFParserBuilder.create().source(inputStream).lang(Lang.RDFXML).toModel();

        // Is there a more reliable way to find the
        // "http://www.gutenberg.org/2009/pgterms/ebook" element that is what we're
        // really interested in?
        final ResIterator resourceIterator = model.listResourcesWithProperty(DCTerms.title);
        if (resourceIterator.hasNext()) {
            final Resource resource = resourceIterator.nextResource();
            return new PgEbook(resource);
        } else {
            throw new NoSuchElementException("Could not find suitable resource for PgEbook");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final long ebookNumber = getEbookNumber();
        result = prime * result + (int) (ebookNumber ^ (ebookNumber >>> 32));
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
        PgEbook other = (PgEbook) obj;
        if (getEbookNumber() != other.getEbookNumber())
            return false;
        return true;
    }
}