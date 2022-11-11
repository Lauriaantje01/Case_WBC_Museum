package org.example;

import org.example.models.example.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class menuTests {

    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();
    Scanner scanner = new Scanner(System.in);

    @Test
    void startMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the pilot of the Walter Bosch Complex museum admin system. Continue?");
        String input = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (input.equals("yes")) {
                System.out.println("Hello, proceed?");
                input = scanner.nextLine();
            }
            if (input.equals("no"))
                proceed = false;
        }
    }

    @Test
    void findingDepotLocationFunction() {
        createMuseum();

        String locationQuery = "SELECT l from Location l WHERE l = 'Depot'";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        Location depot = jpqlQueryLocation.getSingleResult();

        System.out.println(depot);

    }

    @Test
    void addArtworkMenu() {
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

                System.out.println(inputTitle + " created in " + inputYear + ". What is the name of the artist?");
                inputArtistName = scanner.nextLine();
                allInputDone = true;
            } catch (java.lang.NumberFormatException e) {
                System.out.println("Make sure you only use numbers for the year of the artwork, please type again");
            } catch (Exception e) {
                System.out.println("Something went wrong with the input, please try again.");
            }
        }

        Artist artist = new Artist(inputArtistName);
        Artwork artwork = new Artwork(inputTitle, artist, inputYear);

        System.out.println("\nThe following artwork has been created: " + artwork.toString() + " The work will be placed" +
                "by default in the depot. Type 1 to proceed");
        String answerProceed = scanner.nextLine();
        if (answerProceed.equals("1")) {
            executeTransaction(em -> {
                em.persist(artist);
                em.persist(artwork);
            });
            System.out.println("\nYou added the following object to the database: \n" +
                    artwork.toString());
        } else System.out.println("Mission aborted");

        System.out.println("You are now returned to the main menu\n\n\n\n\n\n");

    }

    @Test
    @DisplayName("Proceed method, a method that gives back a boolean whether the user wants to proceed or not")
    void testProceedMethod() {
        boolean proceed = proceed();
        System.out.println(proceed);
    }

    @Test
    @DisplayName("Proceed method, a method that gives back a boolean whether the user wants to proceed or not")
    boolean proceed() {
        System.out.println("Type 1 to proceed or 2 to exit");
        String answerProceed = scanner.nextLine();
        boolean whileSwitch = false;

        while (!(whileSwitch)) {
            if (answerProceed.equals("1")) {
                return true;
            } else if (answerProceed.equals("2")) {
                return false;

            } else System.out.println("Try again typing either 1 or 2");
            answerProceed = scanner.nextLine();
        }
        return false;
    }

    @Test
    @DisplayName("Finding a way to prevent that the same artist is created.")
    void findArtistName() {
        createMuseum();
        String inputArtistName = "Ernst";

        String queryString = "SELECT a FROM Artist a where a.name LIKE :name";
        TypedQuery<Artist> query = em.createQuery(queryString, Artist.class);
        query.setParameter("name", "%" + inputArtistName + "%");
        Artist artist = query.getSingleResult();

        System.out.println(artist.getName());
        assertThat(artist.getName()).isEqualTo("Max Ernst");
    }

    @Test
    @DisplayName("Same query as test above, but added in the method which will be used in the menu")
    Artist inMethodFindDuplicateArtist() {
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
                }

            } catch (javax.persistence.NoResultException e) {
                allInputDone = true;

            } catch (Exception e) {
                System.out.println("Something went wrong with the input, please try again.");
            }
        }
        return new Artist(inputArtistName);
    }

    @Test
    @DisplayName("Newly created find duplicate artist where we also take into account capitol letters")
    void testDuplicate2Method() {
        createMuseum();
        String inputArtistName = "constantin";

        Artist duplicateArtist = findDuplicateArtist2(inputArtistName);

        System.out.println(duplicateArtist.getName());
        assertThat(duplicateArtist.getName()).isEqualTo("Constantin Brancusi");
    }

    @Test
    @DisplayName("Quickly checking what exceptions is thrown when scanner input can not be cast to Long")
    void testLongException() {
        System.out.println("Type a long (but not really because this is a test and I need the name of the exception)");
        Long longID= Long.parseLong(scanner.nextLine());

        // it is a NumberformatException!
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

    private Artist findDuplicateArtist(String inputName) {
        String queryString = "SELECT a FROM Artist a where a.name LIKE :name";
        TypedQuery<Artist> query = em.createQuery(queryString, Artist.class);
        query.setParameter("name", "%" + inputName + "%");
        return query.getSingleResult();
    }

    private Artist findDuplicateArtist2(String inputName) {
        String queryAllNamesString = "SELECT a.name FROM Artist a";
        TypedQuery<String> queryAllNames = em.createQuery(queryAllNamesString, String.class);
        List<String> namesArtists = queryAllNames.getResultList();
        String duplicateName = "XXXXXXX";
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
            if (answerProceed.equals("2")) {
                return false;
            } else System.out.println("Try again typing either 1 or 2");
            answerProceed = scanner.nextLine();
        }
        return false;
    }
}
