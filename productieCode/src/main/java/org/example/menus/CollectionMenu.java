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
        System.out.println("\n\n\n\n\nWhat would you like to see?");
        printSelectionArtworksToConsole();

        System.out.println("\nType 1 If you would like to update one of these works" +
                "\nType 2 to show another selection of works" +
                "\nType 0 to return to the main menu");

        String userInput = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (userInput.equals("1")) {
                try {
                    Artwork artworkToChange = findArtworkWithID();
                    changeLocation(artworkToChange);
                } catch (NoResultException e) {
                    proceed = false;
                }
                System.out.println("You return to the main menu\n\n\n\n\n\n\n\n");
                proceed = false;
            } else if (userInput.equals("2")) {
                startCollectionMenu();
                proceed = false;
            } else if (userInput.equals("0")) {
                proceed = false;
                System.out.println("You return to the main menu\n\n\n\n\n\n\n\n");
            } else {
                System.out.println("Try again typing either 1, 2 or 0");
                userInput = scanner.nextLine();
            }
        }
    }

/*       Note that for the onloan there is a seperate function called, ensuring that a contract for the loan is created
   Since this method has its own persist (that of the artwork and the loan contract) the other change locations need
  their own persist statements.
 */
    private void changeLocation(Artwork artwork) {
        List<Location> locations = findAllLocations();
        System.out.println("Selected artwork: " + artwork.toString());

        // Act
        System.out.println("\n\n\n\n\nType 1 to move the artwork to the " + locations.get(0) + " , type 2 for moving it to the "
                + locations.get(1) + " , type 3 when the artwork goes on loan");

        try {
            int userInput = Integer.parseInt(scanner.nextLine());
            Location locationInput = null;

            if (userInput > 0 && userInput <= 3) {
                if (userInput == 3) {
                    locationInput = locations.get(2);
                    moveArtworkOnLoan(artwork, locationInput);
                }
                else {
                    if (userInput == 1) {
                        locationInput = locations.get(0);
                    }
                    if (userInput == 2) {
                        locationInput = locations.get(1);
                    }
                    artwork.moveTo(locationInput);
                    executeTransaction(em -> {
                        em.persist(artwork);
                    });
                }
                em.clear();
                System.out.println(artwork.toString() + " was succesfully moved to " + locationInput.toString());
            } else System.out.println("\"Try again typing either 1, 2 or 3");

        } catch (NumberFormatException e) {
            System.out.println("\"Try again typing either 1, 2 or 3");

        }

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

    // Add catch for no result exception => try again or return to collection menu.
    private Artwork findArtworkWithID() {
        System.out.println("\nType ID number of artwork you want to change (or 0 to return to the main menu)");
        Long inputID = getValidIDLong();

        boolean proceed = true;
        Artwork foundArtwork = null;

        while (proceed) {
            if (inputID == 0) {
                throw new NoResultException();
            } else {
                try {
                    foundArtwork = em.find(Artwork.class, inputID);
                    if (foundArtwork == null) {
                        throw new NoResultException();
                    } else
                    return foundArtwork;
                } catch (NoResultException e) {
                    System.out.println("Artwork not found, try again (or 0 to return to the main menu)");
                    inputID = getValidIDLong();
                }
            }
        }
        return foundArtwork;
    }

    private boolean moveArtworkOnLoan(Artwork artwork, Location onLoan) {
        System.out.println("To move an artwork to on loan, please add the following for the loan contract:" +
                "What is the address?");
        Scanner scanner = new Scanner(System.in);
        String address = scanner.nextLine();
        System.out.println("\n\n" + artwork + " will go on loan at the following address: " + address);

        BruikleenContract bruikleenContract = new BruikleenContract(artwork, address);
        artwork.setBruikleenContract(bruikleenContract);
        boolean successMove = artwork.moveTo(onLoan);

        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(bruikleenContract);
        });
        return successMove;
    }

    private void printSelectionArtworksToConsole() {
//        Mapping out the options:
        System.out.println("1) All artworks in the collection" +
                "\n2) All artworks at " + findAllLocations().get(0).toString() +
                "\n3) All artworks at " + findAllLocations().get(1).toString() +
                "\n4) All artworks at " + findAllLocations().get(2).toString());


        String userInput = scanner.nextLine();
        boolean proceed = true;

        // could make this conditional (the else if statements), but that would mean that the input needs to be
        // int and not String so the option can be user input == 0 || userinput < locations.size()
        // Also would presume here that the userinput equals the ID of the location, this can be mapped better.

        while (proceed) {
            if (userInput.equals("1")) {
                List<Artwork> artwork = findAllArtworks();
                for (Artwork a : artwork) {
                    System.out.println("ID: " + a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput.equals("2")) {
                printArtworksAtLocation(0);
                proceed = false;
            } else if (userInput.equals("3")) {
                printArtworksAtLocation(1);
                proceed = false;
            } else if (userInput.equals("4")) {
                printArtworksAtLocation(2);
                proceed = false;
            } else {
                System.out.println("Try again typing either 1, 2, 3 or 4");
                userInput = scanner.nextLine();
            }
        }
    }

    private Long getValidIDLong() {
        boolean validID = true;
        Long inputID = 0L;

        while (validID)
            try {
                inputID = Long.parseLong(scanner.nextLine());
                validID = false;
            } catch (NumberFormatException e) {
                System.out.println("Try again, that was not a number");
            }

        return inputID;
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

    private void printArtworksAtLocation(int index) {
        var locations = findAllLocations();
        Location location = locations.get(index);

        List<Artwork> artwork = location.getArtworks();
        System.out.println("\n\n\n\n\n\n\n\n\n\nThe following artworks are at " + location.toString());

        for (Artwork a : artwork) {
            System.out.println("ID: " + a.getId() + " " + a.toString());
        }

    }

}
