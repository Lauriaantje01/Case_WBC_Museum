package org.example.menus;

import org.example.models.example.Artist;
import org.example.models.example.Artwork;
import org.example.models.example.Location;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Scanner;
import java.util.function.Consumer;

public class AddArtworkMenu {

    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();
    Scanner scanner = new Scanner(System.in);

    void startAddArtworkMenu() {
        Artist artist = createArtist();
        Artwork artwork = createArtwork(artist);

        System.out.println("\nThe following artwork has been created: " + artwork.toString() + " The work will be placed" +
                "by default in the depot");

        if (proceed()) {
            executeTransaction(em -> {
                em.persist(artist);
                em.persist(artwork);
            });
            System.out.println("\nYou added the following object to the database: \n" +
                    artwork.toString());
        } else System.out.println("Mission aborted");

        System.out.println("You are now returned to the main menu\n\n\n\n\n\n");

    }

    private Artwork createArtwork(Artist artist) {
        boolean allInputDone = false;

        String inputTitle = "";
        int inputYear = 0;
        String inputArtistName = "";

        while (allInputDone == false) {
            try {
                System.out.println("Type the title of the artwork");
                inputTitle = scanner.nextLine();

                System.out.println("What is the year of the artwork?");
                inputYear = Integer.parseInt(scanner.nextLine());

                System.out.println(inputTitle + " created in " + inputYear + " by " + artist.getName());
                allInputDone = true;
            } catch (java.lang.NumberFormatException e) {
                System.out.println("Make sure you only use numbers for the year of the artwork, please type again");
            } catch (Exception e) {
                System.out.println("Something went wrong with the input, please try again.");
            }
        }

        return new Artwork(inputTitle, artist, inputYear, getDepot());
    }

    private Artist createArtist() {
        boolean allInputDone = false;
        String inputArtistName = "";

        while (allInputDone == false) {
            try {
                System.out.println("What is the name of the artist?");
                inputArtistName = scanner.nextLine();
                allInputDone = true;
            } catch (Exception e) {
                System.out.println("Something went wrong with the input, please try again.");
            }
        }
        return new Artist(inputArtistName);
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

    private Location getDepot() {
        String locationQuery = "SELECT l from Location l WHERE l.name = 'Depot'";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        return jpqlQueryLocation.getSingleResult();
    }

    private boolean proceed() {
        System.out.println("Type 1 to proceed or 2 to exit");
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
}
