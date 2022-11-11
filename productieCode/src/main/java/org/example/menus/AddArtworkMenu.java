package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class AddArtworkMenu {

    EntityManagerFactory emf;
    EntityManager em;
    Scanner scanner = new Scanner(System.in);

    public AddArtworkMenu(EntityManagerFactory emf) {
        this.emf = emf;
        em = emf.createEntityManager();
    }

    void startAddArtworkMenu() {
        System.out.println("\n\n\n\n\nWe start with the artist of the new artwork");
        Artist artist = createArtist();
        Artwork artwork = createArtwork(artist);

        System.out.println("\nThe following artwork has been created: " + artwork.toString() + "\nThe work has been " +
                "placed by default in the depot.\n" +
                "Type 1 to save the artwork, or 0 to return to main menu");


        if (proceed1or2()) {
            executeTransaction(em -> {
                em.persist(artist);
                em.persist(artwork);
            });
            System.out.println("\n\n\n\n\n\n\n\n\n\nYou added the following object to the database: \n" +
                    artwork.toString());
        }
        System.out.println("You are now returned to the main menu\n\n\n\n\n\n\n\n\n");
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
                boolean yearInput = false;

                while (!(yearInput)) {
                    if (inputYear > LocalDate.now().getYear()) {
                        System.out.println("Was this artwork made in the future? Try again");
                        inputYear = Integer.parseInt(scanner.nextLine());
                    } else {
                        yearInput = true;
                    }
                }
                allInputDone = true;
            } catch (java.lang.NumberFormatException e) {
                System.out.println("Make sure you only use numbers for the year of the artwork, please type again");
            } catch (Exception e) {
                System.out.println("Something went wrong with the input, please try again.");
            }
        }
        return new Artwork(inputTitle, artist, inputYear, getDepot());
    }

    //    Note that the method findDuplicateArtist might send a NoResultException. The createArtist method catches this
//    exception and continues to run the program, thus accepting that the artist is not yet in the database.
    private Artist createArtist() {
        boolean allInputDone = false;
        String inputArtistName = "";

        while (allInputDone == false) {
            try {
                System.out.println("What is the name of the artist?");
                inputArtistName = scanner.nextLine();

                Artist duplicateArtist = findDuplicateArtist(inputArtistName);

                System.out.println("Artist with the following name already exists: " + duplicateArtist.getName() +
                        "\nDo you want to add the artwork to this existing artist?" +
                        "\nType 1 to add to existing artist, Type 2 to create a new artist with the same name");
                if (proceed1or2() == true) {
                    return duplicateArtist;
                } else allInputDone = true;

            } catch (javax.persistence.NoResultException e) {
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
        em.clear();
    }

    private Location getDepot() {
        String locationQuery = "SELECT l from Location l WHERE l.name = 'Depot'";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        return jpqlQueryLocation.getSingleResult();
    }

    private Artist findDuplicateArtist(String inputName) {
        String queryAllNamesString = "SELECT a.name FROM Artist a";
        TypedQuery<String> queryAllNames = em.createQuery(queryAllNamesString, String.class);
        List<String> namesArtists = queryAllNames.getResultList();
        String duplicateName = "XXX";
        for (String name : namesArtists) {
            if (name.toUpperCase().contains(inputName.toUpperCase())) {
                duplicateName = name;
            }
        }

        String queryDuplicateString = "SELECT a FROM Artist a where a.name LIKE :name";
        TypedQuery<Artist> queryDuplicate = em.createQuery(queryDuplicateString, Artist.class);
        queryDuplicate.setParameter("name", duplicateName);
        return queryDuplicate.getSingleResult();
    }

    private boolean proceed1or2() {
        String answerProceed = scanner.nextLine();
        boolean whileSwitch = false;

        while (!(whileSwitch)) {
            if (answerProceed.equals("1")) {
                return true;
            }
            if (answerProceed.equals("0")) {
                return false;
            } else System.out.println("Try again typing either 1 or 0");
            answerProceed = scanner.nextLine();
        }
        return false;
    }
}
