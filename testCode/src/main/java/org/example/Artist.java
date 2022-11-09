package org.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private int yearOfBirth;
    private int yearOfDeath;
    private Styles style;

    @OneToMany(mappedBy = "artist")
    @Column (name = "Artist_artworks")
    private Collection<Artwork> artworks = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Artist(String name, int year, int death, Styles style) {
        setName(name);
        setYearOfBirth(year);
        setYearOfDeath(death);
        setStyle(style);
    }
    public Artist(String name) {
        setName(name);
    }

    public Artist(){
        setName("Unknown");
    }

    public Collection<Artwork> getArtworks() {
        return artworks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int year) {
        this.yearOfBirth = year;
    }

    public int getYearOfDeath() {
        return yearOfDeath;
    }

    public void setYearOfDeath(int death) {
        this.yearOfDeath = death;
    }

    public Styles getStyle() {
        return style;
    }

    public void setStyle(Styles style) {
        this.style = style;
    }

    public void addArtwork(Artwork artwork) {
        artworks.add(artwork);
    }

}
