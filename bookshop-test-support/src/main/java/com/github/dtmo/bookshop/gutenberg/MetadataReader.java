package com.github.dtmo.bookshop.gutenberg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import com.github.dtmo.bookshop.opensearch.BookDocument;

public class MetadataReader {
    public static class PGTerms {
        private static final Model model = ModelFactory.createDefaultModel();
        /**
         * Creation/Production Credits Note.
         * 
         * @see https://www.loc.gov/marc/bibliographic/bd508.html
         */
        public static final Property marc508 = model.createProperty("http://www.gutenberg.org/2009/pgterms/marc508");

        /**
         * Summary, Etc.
         * 
         * @see https://www.loc.gov/marc/bibliographic/bd520.html
         */
        public static final Property marc520 = model.createProperty("http://www.gutenberg.org/2009/pgterms/marc520");

        public static final Property name = model.createProperty("http://www.gutenberg.org/2009/pgterms/name");
        public static final Property birthdate = model
                .createProperty("http://www.gutenberg.org/2009/pgterms/birthdate");
        public static final Property deathdate = model
                .createProperty("http://www.gutenberg.org/2009/pgterms/deathdate");
        public static final Property alias = model.createProperty("http://www.gutenberg.org/2009/pgterms/alias");
        public static final Property webpage = model.createProperty("http://www.gutenberg.org/2009/pgterms/webpage");
    }

    public static void main(final String[] args) throws Exception {
        final Model model = ModelFactory.createDefaultModel();

        Files.walkFileTree(Path.of("/home/dtmor/rdf-files/cache/epub"),
                new RdfFileVisitor(model));

        final ResIterator resourceIterator = model.listResourcesWithProperty(DCTerms.title);
        while (resourceIterator.hasNext()) {
            final Resource resource = resourceIterator.next();
            final URI resourceUri = new URI(resource.getURI());
            final Path resourceUriPath = Path.of(resourceUri.getPath());
            final long ebookId = Long.parseLong(resourceUriPath.getName(resourceUriPath.getNameCount() - 1).toString());

            final String title = model.getProperty(resource, DCTerms.title).getString();
            final String productionCredits = model.getProperty(resource, PGTerms.marc508).getString().lines()
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining(" "));
            ;
            final String summary = model.getProperty(resource, PGTerms.marc520).getString().lines()
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.joining(" "));

            final String language = model.getProperty(resource, DCTerms.language).getObject().asResource()
                    .getProperty(RDF.value).getString();

            final Set<String> subjects = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(model.listObjectsOfProperty(resource, DCTerms.subject),
                            Spliterator.ORDERED), false)
                    .map(RDFNode::asResource)
                    .map(propertyResource -> propertyResource.getProperty(RDF.value).getString())
                    .collect(Collectors.toSet());

            final Set<String> authors = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(model.listObjectsOfProperty(resource, DCTerms.creator),
                            Spliterator.ORDERED), false)
                    .map(RDFNode::asResource)
                    .map(propertyResource -> propertyResource.getProperty(PGTerms.name).getString())
                    .collect(Collectors.toSet());

            final BookDocument bookDocument = BookDocument.builder()
                    .id(ebookId)
                    .price(0)
                    .stockKeepingUnit(String.format("PGEBOOK%s", ebookId))
                    .title(title)
                    .productionCredits(productionCredits)
                    .summary(summary)
                    .language(language)
                    .subjects(subjects)
                    .authors(authors)
                    .build();

            System.out.println(bookDocument);
        }

        // StreamSupport.stream(Spliterators.spliteratorUnknownSize(model.listSubjects(),
        // Spliterator.ORDERED), false)
        // .forEach(System.out::println);
    }

    private static class RdfFileVisitor implements FileVisitor<Path> {
        private final Model model;

        public RdfFileVisitor(final Model model) {
            this.model = model;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            try (final InputStream inputStream = Files.newInputStream(file)) {
                model.read(inputStream, null);

                return FileVisitResult.CONTINUE;
            } catch (final Exception e) {
                System.out.println(file);
                e.printStackTrace();
            }

            return FileVisitResult.TERMINATE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
            return FileVisitResult.TERMINATE;
        }

    }
}
