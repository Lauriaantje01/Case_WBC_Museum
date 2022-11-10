package org.example.models.example;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OnLoan extends Location {

    @OneToMany (mappedBy = "location")
    private List<Artwork> artworks = new ArrayList<>();

    @Override
    boolean add(Artwork a) {
        if (artworks.contains(a)) {
            return false;
        }
        a.getLocation().getArtworks().remove(a);
        artworks.add(a);
        return true;
    }

    @Override
    boolean remove(Artwork a) {
        if (!(artworks.contains(a))) {
            return false;
        } else {
            artworks.remove(a);
            return true;
        }
    }

    @Override
    public List<Artwork> getArtworks() {
        return artworks;
    }

    @Override
    public String toString() {
        return "On loan";
    }
}
