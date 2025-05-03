package com.github.dtmo.bookshop.gutenberg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class PgEbookResourceTest {
    @Test
    void testEqualsHashcode() {
        EqualsVerifier.forClass(PgEbookResource.class).verify();
    }

    @Test
    void testFromInputStream() throws Exception {
        final long ebookNumber = 84;
        final String ebookTitle = "Frankenstein; Or, The Modern Prometheus";
        final Optional<String> ebookProductionCredits = Optional.of(
                "Judith Boss, Christy Phillips, Lynn Hanninen and David Meltzer. HTML version by Al Haines. Further corrections by Menno de Leeuw.");
        final Optional<String> ebookSummary = Optional.of(
                "\"Frankenstein; Or, The Modern Prometheus\" by Mary Wollstonecraft Shelley is a novel written in the early 19th century. The story explores themes of ambition, the quest for knowledge, and the consequences of man's hubris through the experiences of Victor Frankenstein and the monstrous creation of his own making.   The opening of the book introduces Robert Walton, an ambitious explorer on a quest to discover new lands and knowledge in the icy regions of the Arctic. In his letters to his sister Margaret, he expresses both enthusiasm and the fear of isolation in his grand venture. As Walton's expedition progresses, he encounters a mysterious, emaciated stranger who has faced great suffering—furthering the intrigue of his narrative. This stranger ultimately reveals his tale of creation, loss, and the profound consequences of seeking knowledge that lies beyond human bounds. The narrative is set up in a manner that suggests a deep examination of the emotions and ethical dilemmas faced by those who dare to defy the natural order. (This is an automatically generated summary.)");
        final String ebookLanguage = "en";
        final Set<String> ebookSubjects = Set.of("Science fiction", "Horror tales", "Gothic fiction",
                "Scientists -- Fiction", "Monsters -- Fiction",
                "Frankenstein, Victor (Fictitious character) -- Fiction",
                "Frankenstein's monster (Fictitious character) -- Fiction", "PR");

        final long authorNumber = 61;
        final String authorName = "Shelley, Mary Wollstonecraft";
        final Set<String> authorAliases = Set.of("Shelley, Mary Wollstonecraft Godwin", "Shelley, Mary W.");
        final int authorBirthdate = 1797;
        final int authorDeathdate = 1851;
        final String authorWebpage = "https://en.wikipedia.org/wiki/Mary_Wollstonecraft_Shelley";

        try (final InputStream inputStream = PgEbookResourceTest.class.getResourceAsStream("pg84.rdf")) {
            final PgEbookResource pgEbook = PgEbookResource.from(inputStream);

            assertEquals(ebookNumber, pgEbook.getEbookNumber());
            assertEquals(ebookTitle, pgEbook.getTitle());
            assertEquals(ebookProductionCredits, pgEbook.getProductionCredits());
            assertEquals(ebookSummary, pgEbook.getSummary());
            assertEquals(ebookLanguage, pgEbook.getLanguage());
            assertEquals(ebookSubjects, pgEbook.getSubjects());

            final Set<PgAuthorResource> authors = pgEbook.getAuthors();
            assertEquals(1, authors.size());
            final PgAuthorResource author = authors.iterator().next();
            assertEquals(authorNumber, author.getAuthorNumber());
            assertEquals(authorName, author.getName());
            assertEquals(authorAliases, author.getAliases());
            assertEquals(authorBirthdate, author.getBirthdate());
            assertEquals(authorDeathdate, author.getDeathdate());
            assertEquals(authorWebpage, author.getWebpage());
        }
    }
}
