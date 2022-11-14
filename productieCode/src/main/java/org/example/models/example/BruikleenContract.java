package org.example.models.example;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class BruikleenContract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private LocalDate returnDate;

    @OneToOne (mappedBy = "bruikleenContract")
    private Artwork artwork;
    private String address;

    public BruikleenContract(Artwork artwork, String address, LocalDate date) {
        setArtwork(artwork);
        setAddress(address);
        setReturnDate(date);
    }

    public BruikleenContract() {
        address = "Unknown, notify curator!";
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Long getId() {
        return id;
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

    public String toString() {
        return "[Loan contract " + this.id + " for artwork " + artwork.getId() + "\n Address: " + this.address + "\n Return date: " +this.returnDate + "]";
    }

    public LocalDate getReturnDate() {
        return this.returnDate;
    }
}
