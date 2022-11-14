package org.example.menus;

import org.assertj.core.api.LocalDateAssert;
import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
        selectArtworksListAndPrintToConsole();

        System.out.println("\n1) Update the location of an artwork" +
                "\n2) Show another selection of works" +
                "\n3) Retrieve loan contract of artworks on loan" +
                "\n0) Return to the main menu");

        String userInput = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (userInput.equals("1")) {
                try {
                    System.out.println("\nType ID number of artwork you want to select (or 0 to return to the main menu)");
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
            } else if (userInput.equals("3")) {
                showLoanStatus();
                proceed = false;
                System.out.println("You return to the main menu\n\n\n\n\n\n\n\n");
            } else if (userInput.equals("0")) {
                proceed = false;
                System.out.println("You return to the main menu\n\n\n\n\n\n\n\n");
            } else {
                System.out.println("Try again typing either 1, 2 or 0");
                userInput = scanner.nextLine();
            }
        }
    }

    private void showLoanStatus() {
        String locationQuery = "SELECT l from Location l WHERE l.name = 'On Loan'";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        Location onLoanLocation = jpqlQueryLocation.getSingleResult();

        System.out.println("\n\n\n\n\n\n\n\n\n\n\nThe following artworks are on loan:");
        printArtworksAtLocation(onLoanLocation);
        System.out.println("\n\nGive the artwork ID if you want to see the loan contract of an artwork " +
                "(or 0 to return to the main menu).");

        boolean proceed = true;
        while (proceed)
        {
            try {
                Artwork artworkOnLoan = findArtworkWithID();
                System.out.println(artworkOnLoan.getBruikleenContract().toString());
                proceed = false;
            } catch (NoResultException e) {
                // thrown by findArtworkID
                proceed = false;
            } catch (NullPointerException e) {
                // thrown if there is no contract
                System.out.println("\n\n\n\n\n\nLooks like the artwork you selected does not have a loan contract." +
                        "\nTry again with another artwork ID or choose 0 to return to the main menu.");
            }
        }
    }

    /*       Note that for the onloan there is a seperate function called, ensuring that a contract for the loan is created
       Since this method has its own persist (that of the artwork and the loan contract) the other change locations need
      their own persist statements.
     */
    private void changeLocation(Artwork artwork) {
        List<Location> locations = findAllLocations();
        System.out.println("\n\n\n\n\nSelected artwork: " + artwork.toString());

        // Act
        System.out.println("Type 1 to move the artwork to the " + locations.get(0) + ", type 2 for moving it to the "
                + locations.get(1) + ", type 3 when the artwork goes on loan");

        boolean proceed = true;
        while (proceed) {
            try {
                int userInput = Integer.parseInt(scanner.nextLine());
                Location locationInput = null;

                if (userInput > 0 && userInput <= 3) {
                    if (userInput == 3) {
                        locationInput = locations.get(2);
                        moveArtworkOnLoan(artwork, locationInput);
                        proceed = false;
                    } else {
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
                        proceed = false;
                    }
                    em.clear();
                    System.out.println("Artwork with ID " + artwork.getId() + " is now at location " + artwork.getLocation());
                } else System.out.println("Try again typing either 1, 2 or 3");

            } catch (NumberFormatException e) {
                System.out.println("Try again typing either 1, 2 or 3");

            }
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

    // Below method throws NoResultException if user selects 0. Thus when using this method, ensure
    // to add a catch for no result exception to return to collection menu.
    private Artwork findArtworkWithID() {
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
                "\nWhat is the address?");
        String address = scanner.nextLine();

        System.out.println("Till when will the artwork be on loan? The date should be entered in the following format YEAR-MONTH-DAY (e.g. 2024-08-12)");
        LocalDate returnDate = getValidDate();

        System.out.println("\n\n" + artwork + " will go on loan at the following address: " + address);

        BruikleenContract bruikleenContract = new BruikleenContract(artwork, address, returnDate);
        artwork.setBruikleenContract(bruikleenContract);
        boolean successMove = artwork.moveTo(onLoan);

        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(bruikleenContract);
        });
        return successMove;
    }

    private LocalDate getValidDate() {
        LocalDate returnDate = null;
        boolean proceed = true;
        while (proceed) {
            String returnDateString = scanner.nextLine();
            try {
                returnDate = LocalDate.parse(returnDateString);
                proceed = false;
            } catch (DateTimeParseException e) {
                System.out.println("Try again, the date should be entered in the following format YEAR-MONTH-DAY (e.g. 2024-08-12)");
            }
        }

        return returnDate;
    }

    private void selectArtworksListAndPrintToConsole() {
//        Mapping out the options:
        List<Location> locations = findAllLocations();
        System.out.println("1) All artworks in the collection" +
                "\n2) All artworks at " + locations.get(0).toString() +
                "\n3) All artworks at " + locations.get(1).toString() +
                "\n4) All artworks on loan");


        String userInput = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (userInput.equals("1")) {
                List<Artwork> artwork = findAllArtworks();
                for (Artwork a : artwork) {
                    System.out.println("ID: " + a.getId() + " " + a.toString());
                }
                proceed = false;
            } else if (userInput.equals("2")) {
                System.out.println("\n\n\n\n\n\n\n\n\n\nThe following artworks are at " + locations.get(0).toString());
                printArtworksAtLocation(locations.get(0));
                proceed = false;
            } else if (userInput.equals("3")) {
                System.out.println("\n\n\n\n\n\n\n\n\n\nThe following artworks are at " + locations.get(1).toString());
                printArtworksAtLocation(locations.get(1));
                proceed = false;
            } else if (userInput.equals("4")) {
                System.out.println("\n\n\n\n\n\n\n\n\n\nThe following artworks are on loan");
                printArtworksAtLocation(locations.get(2));
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

    private void printArtworksAtLocation(Location location) {
        List<Artwork> artworks = location.getArtworks();

        if (artworks.size() == 0) {
            System.out.println("No artworks are currently at this location / are on loan");
        } else {

            for (Artwork a : artworks) {
                System.out.println("ID: " + a.getId() + " " + a.toString());
            }
        }
    }
}
