package com.github.dtmo.bookshop.entities;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "author")
@Data
@NoArgsConstructor
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @NonNull
    private String name;

    private String alias;

    @Override
    public boolean equals(final Object object) {
        final boolean equal;

        if (this == object) {
            equal = true;
        } else if (object == null) {
            equal = false;
        } else if (object instanceof AuthorEntity) {
            final AuthorEntity other = (AuthorEntity) object;
            equal = Objects.equals(this.name, other.name) && Objects.equals(this.alias, other.alias);
        } else {
            equal = false;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.alias);
    }

    @Override
    public String toString() {
        return String.format("AuthorEntity[id=\"%s\", name=\"%s\", alias=\"%s\"]", this.id, this.name, this.alias);
    }
}
