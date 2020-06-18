package com.hw.aggregate.catalog.model;

import com.hw.aggregate.catalog.CatalogRepo;
import com.hw.aggregate.catalog.command.CreateCatalogCommand;
import com.hw.aggregate.catalog.command.UpdateCatalogCommand;
import com.hw.aggregate.catalog.exception.CatalogNotFoundException;
import com.hw.shared.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Entity
@Table(name = "biz_catalog")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Catalog extends Auditable {

    @Id
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    public static Catalog create(Long id, CreateCatalogCommand command, CatalogRepo repo) {
        return repo.save(new Catalog(id, command));
    }

    public static Catalog get(Long id, CatalogRepo repo) {
        Optional<Catalog> findById = repo.findById(id);
        if (findById.isEmpty())
            throw new CatalogNotFoundException();
        return findById.get();
    }

    public static void update(Long id, UpdateCatalogCommand command, CatalogRepo repo) {
        Catalog catalog = get(id, repo);
        catalog.setTitle(command.getTitle());
        repo.save(catalog);
    }

    public static void delete(Long id, CatalogRepo repo) {
        Catalog catalog = get(id, repo);
        repo.delete(catalog);
    }

    private Catalog(Long id, CreateCatalogCommand command) {
        this.id = id;
        this.title = command.getTitle();
    }
}
