package org.example.models.example;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Depot extends Location {

    @OneToMany (mappedBy = "location")
    private List<Artwork> artworks = new ArrayList<>();

    @Override
    public boolean add(Artwork a) {
        if (artworks.contains(a)) {
            return false;
        }
        artworks.add(a);
        return true;
    }

    String name = "Depot";

    @Override
    public boolean remove(Artwork a) {
        artworks.remove(a);
        return true;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public String toString() {
        return "depot";
    }
}
