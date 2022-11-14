package org.example;

import javax.persistence.*;

@Entity
public class Artwork {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artwork_id")
    @SequenceGenerator(sequenceName = "artwork_id", name = "artwork_id")
    private Long id;

    private String title;
    @ManyToOne
    private Artist artist;
    private int year;
    @ManyToOne
    private Location location;

    @OneToOne
    private BruikleenContract bruikleenContract;

    public Location getLocation() {
        return location;
    }

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

    public Artwork(String title, Artist artist, int year, Location location) {
        setTitle(title);
        setArtist(artist);
        setYear(year);
        this.location = location;
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
            this.location = location;
            return true;
        } else {
            System.out.println("Artwork with ID " + this.id + " was already in the location or something else went wrong");
            return false;
        }
    }

    public BruikleenContract getBruikleenContract() {
        return bruikleenContract;
    }

    public void setBruikleenContract(BruikleenContract bruikleenContract) {
        this.bruikleenContract = bruikleenContract;
    }

    @Override
    public String toString() {
        return "[Title: " + title + ", Artist: " + artist.getName() + ", Year: " + year + ", Location: " + location +"]";
    }


}
