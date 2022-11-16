package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
        // First allow the user to look at the artworks already available (so they can already see the artwork IDs)
        selectArtworksListAndPrintToConsole();

        String startMenuText = "\n\n\n\n\nWhat would you like to do?" +
                "\n1) Update the location of an artwork" +
                "\n2) Show another selection of artworks" +
                "\n3) Retrieve loan contract of artworks on loan" +
                "\n0) Return to the main menu";

        String exitMenuMessage = "You return to the main menu\n\n\n\n\n\n\n\n";
        boolean proceed = true;

        while (proceed) {
            int userInput = validateUserMenuChoice(3, startMenuText);
            if (userInput == 1) {
                try {
                    changeLocationMenu();
                } catch (NoResultException e) {
                }
                proceed = false;
                System.out.println(exitMenuMessage);
            } else if (userInput == 2) {
                selectArtworksListAndPrintToConsole();
            } else if (userInput == 3) {
                showLoanStatus();
            } else {
                System.out.println(exitMenuMessage);
                proceed = false;
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
                "(or 0 to return to the collection menu).");

        boolean proceed = true;
        while (proceed) {
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

    /*       A seperate function is called for objects on loan, ensuring that a contract for the loan is created
      Since this method has its own persist (that of the artwork and the loan contract) the other change locations need
      their own persist statements.
     */
    private void changeLocationMenu() {
        System.out.println("\nType ID number of artwork you want to select (or 0 to return to the main menu)");
        Artwork artwork = findArtworkWithID();

        List<Location> locations = findAllLocations();
        System.out.println("\n\n\n\n\nSelected artwork: " + artwork.toString());

        String changeLocationMenuText = "Type 1 to move the artwork to the " + locations.get(0) + ", type 2 for moving it to the "
                + locations.get(1) + ", type 3 when the artwork goes on loan (or 0 to exit to the previous menu)";

        int userInput = validateUserMenuChoice(3, changeLocationMenuText);

        Location locationInput = null;

        if (userInput == 0) {
            return;
        } else if (userInput == 1) {
            locationInput = locations.get(0);
            changeLocation(artwork, locationInput);
        } else if (userInput == 2) {
            locationInput = locations.get(1);
            changeLocation(artwork, locationInput);
        } else if (userInput == 3) {
            locationInput = locations.get(2);
            moveArtworkOnLoan(artwork, locationInput);
        }
        em.clear();
        System.out.println("Artwork with ID " + artwork.getId() + " is now at location " + artwork.getLocation());
    }

    private void changeLocation(Artwork artwork, Location locationInput) {
        artwork.moveTo(locationInput);
        executeTransaction(em -> {
            em.persist(artwork);
        });
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

    //     Below method throws NoResultException if user selects 0. Thus, when using this method, ensure
//     to add a catch for no result exception to return to collection menu.
    private Artwork findArtworkWithID() {
        Long inputID = getValidIDLong();

        boolean proceed = true;
        Artwork foundArtwork = null;

        while (proceed) {
            if (inputID == 0) {
                return null;
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

    private void moveArtworkOnLoan(Artwork artwork, Location onLoan) {
        System.out.println("To move an artwork to on loan, please add the following for the loan contract:" +
                "\nWhat is the address?");
        String address = scanner.nextLine();

        System.out.println("Till when will the artwork be on loan? The date should be entered in the following format YYYY-MM-DD (e.g. 2024-08-12)");
        LocalDate returnDate = getValidDate();

        System.out.println("\n\n" + artwork + " will go on loan at the following address: " + address);

        BruikleenContract bruikleenContract = new BruikleenContract(artwork, address, returnDate);
        artwork.goesOnLoan((OnLoan) onLoan, bruikleenContract);

        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(bruikleenContract);
        });
        em.clear();
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
        System.out.println("\n\n\n\n\nWhat would you like to see?");
//        Mapping out the options:
        List<Location> locations = findAllLocations();
        String printArtworksMenuText = "1) All artworks in the collection" +
                "\n2) All artworks at " + locations.get(0).toString() +
                "\n3) All artworks at " + locations.get(1).toString() +
                "\n4) All artworks on loan" +
                "\n0) Return to previous menu";

        int userInput = validateUserMenuChoice(4, printArtworksMenuText);
        String artworksAreAt = "\n\n\n\n\n\n\n\n\n\nThe following artworks are at ";

        if (userInput == 1) {
            printAllArtworksToConsole();
        } else if (userInput == 2) {
            System.out.println(artworksAreAt + locations.get(0).toString());
            printArtworksAtLocation(locations.get(0));
        } else if (userInput == 3) {
            System.out.println(artworksAreAt + locations.get(1).toString());
            printArtworksAtLocation(locations.get(1));
        } else if (userInput == 4) {
            System.out.println("\n\n\n\n\n\n\n\n\n\nThe following artworks are on loan");
            printArtworksAtLocation(locations.get(2));
        } else {
            return;
        }
    }

    private void printAllArtworksToConsole() {
        List<Artwork> artwork = findAllArtworks();
        for (Artwork a : artwork) {
            System.out.println("    ID: " + a.getId() + " " + a.toString());
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
            System.out.println("    No artworks are currently at this location / are on loan");
        } else {

            for (Artwork a : artworks) {
                System.out.println("    ID: " + a.getId() + " " + a.toString());
            }
        }
    }

    private int validateUserMenuChoice(int noOfOptions, String menuText) {
        boolean proceed = true;
        int userInput = 0;

        System.out.println(menuText);

        while (proceed) {
            try {
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput > 0 || userInput <= noOfOptions) {
                    return userInput;
                } else if (userInput == 0) {
                    return 0;
                } else System.out.println("Try again typing the number of one of the options");
            } catch (NumberFormatException e) {
                System.out.println("Try again typing the number of one of the options");
            }
        }
        return userInput;
    }
}
