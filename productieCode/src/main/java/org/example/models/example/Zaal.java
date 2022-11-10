package org.example.models.example;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Zaal extends Location {

    String name = "Zaal";
    @OneToMany(mappedBy = "location")
    private List<Artwork> artworks = new ArrayList<>();

    @Override
    public boolean add(Artwork a) {
        if (artworks.contains(a)) {
            return false;
        }
        a.getLocation().getArtworks().remove(a);
        artworks.add(a);
        return true;
    }

    @Override
    public boolean remove(Artwork a) {
        artworks.remove(a);
        return true;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public String toString() {
        return "zaal";
    }
}
