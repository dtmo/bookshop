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

import lombok.Data;

/**
 * PgEbookResource provides convenient methods to access information from an RDF
 * representation of a <a href="https://www.gutenberg.org/">Project
 * Gutenberg</a> eBook.
 */
@Data
public final class PgEbookResource {
    private final Resource ebookResource;

    /**
     * Constructs a new instance of PgEbookResource from an RDF resource.
     * 
     * @param ebookResource The RDF resource representing the eBook.
     */
    public PgEbookResource(final Resource ebookResource) {
        this.ebookResource = ebookResource;
    }

    /**
     * @return The unique Project Gutenberg catalog number (or assession number).
     */
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

    /**
     * @return The title.
     */
    public String getTitle() {
        return ebookResource.getProperty(DCTerms.title).getString();
    }

    /**
     * @return The production credits.
     */
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

    /**
     * @return The summary description of the book.
     */
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

    /**
     * @return The <a href=
     *         "https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes">ISO
     *         639 language code</a> of the language in which the book is written.
     */
    public String getLanguage() {
        return ebookResource.getProperty(DCTerms.language).getObject().asResource()
                .getProperty(RDF.value).getString();
    }

    /**
     * @return The set of subject descriptions relating to the book's contents.
     */
    public Set<String> getSubjects() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(ebookResource.listProperties(DCTerms.subject),
                        Spliterator.ORDERED), false)
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(propertyResource -> propertyResource.getProperty(RDF.value).getString())
                .collect(Collectors.toSet());
    }

    /**
     * @return The set of authors who contributed to the book.
     */
    public Set<PgAuthorResource> getAuthors() {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(ebookResource.listProperties(DCTerms.creator),
                        Spliterator.ORDERED), false)
                .map(Statement::getObject)
                .map(RDFNode::asResource)
                .map(PgAuthorResource::new)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a new instance of PgEbookResource from an input stream containing RDF
     * data.
     * 
     * @param inputStream The input stream from which to read RDF data.
     * @return The new PgEbookResource construted from the RDF data.
     */
    public static PgEbookResource from(final InputStream inputStream) {
        final Model model = RDFParserBuilder.create().source(inputStream).lang(Lang.RDFXML).toModel();

        // TODO: Is there a more reliable way to find the
        // "http://www.gutenberg.org/2009/pgterms/ebook" element that is what we're
        // really interested in?
        final ResIterator resourceIterator = model.listResourcesWithProperty(DCTerms.title);
        if (resourceIterator.hasNext()) {
            final Resource resource = resourceIterator.nextResource();
            return new PgEbookResource(resource);
        } else {
            throw new NoSuchElementException("Could not find suitable resource for PgEbook");
        }
    }
}