package org.example.models.example;

import javax.persistence.*;

@Entity
public class BruikleenContract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne (mappedBy = "bruikleenContract")
    private Artwork artwork;
    private String address;

    public BruikleenContract(Artwork artwork, String address) {
        setArtwork(artwork);
        setAddress(address);
    }

    public BruikleenContract() {
        address = "Unknown, notify curator";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
