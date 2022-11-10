package org.example.models.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id")
    @SequenceGenerator(sequenceName = "artist_id", name = "artist_id")
    private Long id;

    private String name;
    private int yearOfBirth;
    private int yearOfDeath;

    @OneToMany(mappedBy = "artist")
    @Column (name = "Artist_artworks")
    private Collection<Artwork> artworks = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Artist(String name, int year, int death) {
        setName(name);
        setYearOfBirth(year);
        setYearOfDeath(death);
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

    public void addArtwork(Artwork artwork) {
        artworks.add(artwork);
    }

}
