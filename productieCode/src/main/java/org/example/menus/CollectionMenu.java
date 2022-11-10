package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/*
The collection menu sorts out all actions that have to do with updating existing artworks in the database.
A function for changing details of the artwork I deem less interesting as these do not tend to change, perhaps I could
come up with something else that is more interesting to change rather than make endless menus of changing certain aspects
 */

public class CollectionMenu {

    EntityManagerFactory emf;
    EntityManager em;
    Scanner scanner = new Scanner(System.in);
    public CollectionMenu (EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
    }

    public void startCollectionMenu() {
        // Could add option to look at artists instead for updates....
        System.out.println("Which of the following artworks would you like to update?\n");
        Artwork artwork = queryWithId();

        System.out.println("What would you like to change? Type the corresponding number:\n" +
                "1) Change Location of artwork");

        int userInput = Integer.parseInt(scanner.nextLine());

        if (userInput == 1) {
            changeLocation(artwork);
        }

    }

    private void changeLocation(Artwork artwork) {
        List<Location> locations = findAllLocations();
        Location locationInput = null;

        // Act
        System.out.println("Type 1 to move the artwork to the " + locations.get(0) + " , type 2 for moving it to the "
                + locations.get(1) + " , type 3 when the artwork goes on loan");
        int userInput = Integer.parseInt(scanner.nextLine());

        if (userInput == 1) {
            locationInput = locations.get(0);
            artwork.moveTo(locations.get(0));

        } else if (userInput == 2) {
            locationInput = locations.get(1);
            artwork.moveTo(locations.get(1));

        } else if (userInput == 3) {
            locationInput = locations.get(2);
            moveArtworkOnLoan(artwork, locationInput);

        } else {
            System.out.println("Something went wrong, contact administrator or try again.");
        }

        executeTransaction(em -> {
            em.persist(artwork);
        });
    }


    private boolean proceed1or2() {
        String answerProceed = scanner.nextLine();
        boolean whileSwitch = false;

        while (!(whileSwitch)) {
            if (answerProceed.equals("1")) {
                return true;
            }
            if (answerProceed.equals("2")) {
                return false;
            } else System.out.println("Try again typing either 1 or 2");
            answerProceed = scanner.nextLine();
        }
        return false;
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
        em.clear();
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

    private Artwork queryWithId() {

        List<Artwork> artwork = findAllArtworks();

        for (Artwork a : artwork) {
            System.out.println(a.getId() + " " + a.toString());
        }

        System.out.println("Type ID number of artwork you want to change");

        Scanner scanner = new Scanner(System.in);

        Long inputID = Long.parseLong(scanner.nextLine());
        Artwork foundArtwork = em.find(Artwork.class, inputID);

        System.out.println(foundArtwork);

        return foundArtwork;
    }

    private boolean moveArtworkOnLoan(Artwork artwork, Location onloan) {
        System.out.println("To move an artwork to on loan, please add the following for the loan contract:" +
                "What is the address?");
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
