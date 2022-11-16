package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class testUpdateMultipleArtworks {

    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();
    Scanner scanner = new Scanner(System.in);

    @Test
    @DisplayName("Writing a code in which user could either show a list of artworks based on location, or a general " +
            "list of all artworks. Console will print all the artworks according to the chosen selection criteria in this" +
            "test")
    void methodToShowListOfArtworks() {
        createMuseum();
        System.out.println("What would you like to see?" +
                "\n1) All artworks in the collection" +
                "\n2) All artworks at " + findAllLocations().get(0).toString() +
                "\n3) All artworks at " + findAllLocations().get(1).toString() +
                "\n4) All artworks at " + findAllLocations().get(2).toString());

        int userInput = Integer.parseInt(scanner.nextLine());
        boolean proceed = true;

        while (proceed) {
            if (userInput == 1) {
                List<Artwork> artwork = findAllArtworks();
                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput == 2) {
                List<Artwork> artwork = findArtworksAtLocation(0);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput == 3) {
                List<Artwork> artwork = findArtworksAtLocation(1);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput == 4) {
                List<Artwork> artwork = findArtworksAtLocation(2);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput == 0) {
                proceed = false;
            } else System.out.println("Try again typing either 1, 2 or 3");
        }

    }

    @Test
    @DisplayName("Moving multiple artworks with user input. Selection is based on the artworks at a certain location")
    void movingMultipleArtworks() {
        // Arrange
        createMuseum();
        Location newLocation = findAllLocations().get(2);
        Location oldLocation = findAllLocations().get(1);
        List<Artwork> artworksToMove = new ArrayList<>();
        for (Artwork a : oldLocation.getArtworks()) {
            artworksToMove.add(a);
        }
        System.out.println(artworksToMove);

        // Act
        for (Artwork a  : artworksToMove) {
            if (newLocation instanceof OnLoan) {
                moveArtworkOnLoan(a, newLocation);
            }
            else {
                a.moveTo(newLocation);
                executeTransaction(em -> {
                    em.persist(a);
                });
            }
        }
        //Assert
        assertThat(artworksToMove.get(0).getLocation()).isEqualTo(newLocation);
    }


    private void executeTransaction(Consumer<EntityManager> consumer) {
        try {
            em.getTransaction().begin();
            consumer.accept(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Something went wrong in the test", e);
        }
    }

    private void createMuseum() {
        Location depot = new Depot();
        Location zaal = new Zaal();
        Location onLoan = new OnLoan();

        Artist maxErnst = new Artist("Max Ernst", 1891, 1976);
        Artwork artwork1 = new Artwork("Europe after the Rain II", maxErnst, 1941, zaal);
        Artwork artwork2 = new Artwork("Blue Forest", maxErnst, 1931, depot);
        Artwork artwork3 = new Artwork("Two Children Are Threatened by a Nightingale", maxErnst, 1924, depot);
        Artist brancusi = new Artist("Constantin Brancusi", 1876, 1957);
        Artwork artwork4 = new Artwork("The Fish", brancusi, 1924, depot);

        executeTransaction(em -> {
            em.persist(maxErnst);
            em.persist(artwork1);
            em.persist(artwork2);
            em.persist(artwork3);
            em.persist(brancusi);
            em.persist(artwork4);
            em.persist(zaal);
            em.persist(depot);
            em.persist(onLoan);
        });

        em.clear();
    }

    private Artwork queryWithId() {

        System.out.println("Type ID number of artwork you want to change");

        Scanner scanner = new Scanner(System.in);

        Long inputID = Long.parseLong(scanner.nextLine());
        Artwork foundArtwork = em.find(Artwork.class, inputID);

        System.out.println(foundArtwork);

        return foundArtwork;
    }


    private List<Artwork> findAllArtworks() {
        String queryFindAllArtworks = "SELECT a from Artwork a";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindAllArtworks, Artwork.class);
        return psqlQuery.getResultList();
    }

    private List<Location> findAllLocations() {
        String locationQuery = "SELECT l from Location l";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        return jpqlQueryLocation.getResultList();
    }

    private List<Artwork> findArtworksAtLocation(int index) {
        Location location = findAllLocations().get(index);
        return location.getArtworks();
    }

    private boolean moveArtworkOnLoan(Artwork artwork, Location onloan) {
        System.out.println("To move the artwork to on loan, please add the following for the loan contract of artwork with ID " + artwork.getId() + "." +
                "\nWhat is the address?");
        Scanner scanner = new Scanner(System.in);
        String address = scanner.nextLine();
        System.out.println(artwork + " will go on loan at the following address: " + address);

        BruikleenContract bruikleenContract = new BruikleenContract(artwork, address);
        artwork.setBruikleenContract(bruikleenContract);
        boolean successMove = artwork.moveTo(onloan);

        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(bruikleenContract);
        });
        return successMove;
    }
}
