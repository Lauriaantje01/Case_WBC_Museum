package org.example;

import java.util.ArrayList;
import java.util.List;

public interface Location {
    List<Artwork> artworks = new ArrayList<>();

    boolean add(Artwork a);

    boolean remove(Artwork a);
}
