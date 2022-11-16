package org.example.models.example;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance (strategy = InheritanceType.SINGLE_TABLE)

public abstract class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id")
    @SequenceGenerator(sequenceName = "location_id", name = "location_id")
    private Long id;

    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    abstract boolean add(Artwork a);

    abstract boolean remove(Artwork a);

    abstract public List<Artwork> getArtworks();
}