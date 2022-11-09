package org.example;

import javax.persistence.*;

@Entity
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToOne
    private Artist artist;
    private int year;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Artwork(String title, Artist artist, int year) {
        setTitle(title);
        setArtist(artist);
        setYear(year);
    }

    public Artwork() {
        setTitle("unknown");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
        artist.addArtwork(this);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean moveTo(Location location) {
        if (location.add(this)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[Title: " + title + ", Artist: " + artist.getName() + ", Year: " + year + "]";
    }


}
