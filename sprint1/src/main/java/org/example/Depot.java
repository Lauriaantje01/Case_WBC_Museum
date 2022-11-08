package org.example;

import java.util.ArrayList;
import java.util.List;

public class Depot implements Location{

    private List<Artwork> artworks = new ArrayList<>();

    @Override
    public boolean add(Artwork a) {
        artworks.add(a);
        return true;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }
}
