package org.example;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Bruikleen {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne (mappedBy = "bruikleen")
    private Artwork artwork;
    private String address;

    public Bruikleen() {
        address = "Unknown, notify curator";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Bruikleen(Artwork artwork, String address) {
        setArtwork(artwork);
        setAddress(address);
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
