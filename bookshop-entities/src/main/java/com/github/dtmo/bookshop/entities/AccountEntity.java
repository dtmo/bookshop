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
@Table(name = "account")
@Data
@NoArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private Long id;

    @NonNull
    private String name;

    @Override
    public boolean equals(final Object object) {
        final boolean equal;

        if (this == object) {
            equal = true;
        } else if (object == null) {
            equal = false;
        } else if (object instanceof AccountEntity) {
            final AccountEntity other = (AccountEntity) object;
            equal = Objects.equals(this.name, other.name);
        } else {
            equal = false;
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return String.format("AccountEntity[id=\"%s\", name=\"%s\"]", this.id, this.name);
    }
}
