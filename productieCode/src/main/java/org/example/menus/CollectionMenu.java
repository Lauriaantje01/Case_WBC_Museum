package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
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

    public CollectionMenu(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
    }

    public void startCollectionMenu() {
        // Could add option to look at artists instead for updates....
        System.out.println("What would you like to see?");
        printRelevantArtworksToConsole();

        System.out.println("Type 1 If you would you like to update one of these works" +
                "\n Type 2 to show another selection of works" +
                "\n Type 0 to return to the main menu");

        String userInput = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (userInput.equals("1")) {
                Artwork artworkToChange = queryWithId();
                changeLocation(artworkToChange);
                proceed = false;
            } else if (userInput.equals("2")) {
                startCollectionMenu();
                proceed = false;
            } else if (userInput.equals("0")) {
                proceed = false;
            } else System.out.println("Try again typing either 1, 2 or 0");
        }
        System.out.println("You return to the main menu\n\n\n\n\n\n");
    }

    //    Note that for the onloan there is a seperate function called, ensuring that a contract for the loan is created
//    Since this method has its own persist (that of the artwork and the loan contract) the other change locations need
//    their own persist statements.
    private void changeLocation(Artwork artwork) {
        List<Location> locations = findAllLocations();
        Location locationInput = null;

        // Act
        System.out.println("Type 1 to move the artwork to the " + locations.get(0) + " , type 2 for moving it to the "
                + locations.get(1) + " , type 3 when the artwork goes on loan");
        String userInput = scanner.nextLine();

        if (userInput.equals("1")) {
            locationInput = locations.get(0);
            artwork.moveTo(locations.get(0));
            executeTransaction(em -> {
                em.persist(artwork);
            });

        } else if (userInput.equals("2")) {
            locationInput = locations.get(1);
            artwork.moveTo(locations.get(1));
            executeTransaction(em -> {
                em.persist(artwork);
            });

        } else if (userInput.equals("3")) {
            locationInput = locations.get(2);
            moveArtworkOnLoan(artwork, locationInput);

        } else {
            System.out.println("Something went wrong, contact administrator or try again.");
        }
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

    // Add catch for no result exception => try again or return to collection menu.
    private Artwork queryWithId() {
        System.out.println("Type ID number of artwork you want to change");

        Long inputID = Long.parseLong(scanner.nextLine());
        boolean proceed = true;

        while (proceed) {
            if (inputID == 0) {
                throw new NoResultException();
            try {
                Artwork foundArtwork = em.find(Artwork.class, inputID);
                proceed = false;
            } catch (NoResultException e) {
                System.out.println("Artwork not found, try again or type 0 to exit the menu");
            }
        }
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

    private List<Artwork> findArtworksAtLocation(int index) {
        Location location = findAllLocations().get(index);
        return location.getArtworks();
    }

    private void printRelevantArtworksToConsole() {
        System.out.println("\n1) All artworks in the collection" +
                "\n2) All artworks at " + findAllLocations().get(0).toString() +
                "\n3) All artworks at " + findAllLocations().get(1).toString() +
                "\n4) All artworks at " + findAllLocations().get(2).toString());

        String userInput = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (userInput.equals("1")) {
                List<Artwork> artwork = findAllArtworks();
                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput.equals("2")) {
                List<Artwork> artwork = findArtworksAtLocation(0);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput.equals("3")) {
                List<Artwork> artwork = findArtworksAtLocation(1);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput.equals("4")) {
                List<Artwork> artwork = findArtworksAtLocation(2);

                for (Artwork a : artwork) {
                    System.out.println(a.getId() + " " + a.toString());
                }
                proceed = false;
            } else System.out.println("Try again typing either 1, 2, 3 or 4");
        }
    }
}
