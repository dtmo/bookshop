package com.github.dtmo.bookshop.gutenberg;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;

public class PGTerms {
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