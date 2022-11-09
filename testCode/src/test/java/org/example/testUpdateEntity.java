package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Styles.DADA;
import static org.example.Styles.SURREALISM;

public class testUpdateEntity {

    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();


    @Test
    @DisplayName("Bouwen van een functie waarbij je een kunstwerk kan updaten in de database. Hier probeer ik een jaar " +
            "te veranderen van een kusntwerk")
    void testUpdateArtworkYearDB() {
        createSeveralArtWorks();

        String queryFindArtwork = "SELECT a from Artwork a where a.year = 1931";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindArtwork, Artwork.class);
        Artwork artwork = psqlQuery.getSingleResult();

        artwork.setYear(1930);
        executeTransaction(em -> {
            em.persist(artwork);
        });

        em.clear();
        String queryRefindArtwork = "SELECT a from Artwork a where a.year = 1930";
        TypedQuery<Artwork> psqlQuery2 = em.createQuery(queryRefindArtwork, Artwork.class);
        Artwork artworkUpdated = psqlQuery2.getSingleResult();

        assertThat(artworkUpdated.getYear()).isEqualTo(1930);
        assertThat(artworkUpdated.getTitle()).isEqualTo(artwork.getTitle());
    }

    @Test
    @DisplayName("Adding a variable to an query")
    void testQueryWithvariable() {
        System.out.println("Type year of artwork you want to find");
        createSeveralArtWorks();
        Scanner scanner = new Scanner(System.in);

        int lookingForYear = Integer.parseInt(scanner.nextLine());
        String queryFindArtwork = "SELECT a from Artwork a where a.year = :lookingForYear";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindArtwork, Artwork.class);
        psqlQuery.setParameter("lookingForYear", lookingForYear);
        Artwork artwork = psqlQuery.getSingleResult();

        System.out.println(artwork);

    }

    @Test
    @DisplayName("Finding an artwork with user input. This time with the find function")
    void testQueryWithID() {
        // Arrange
        createSeveralArtWorks();
        String queryFindArtwork = "SELECT a from Artwork a";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindArtwork, Artwork.class);
        List<Artwork> artwork = psqlQuery.getResultList();

        // Act
        for (Artwork a : artwork) {
            System.out.println(a.getId() + " " + a.toString());
        }

        System.out.println("Type ID number of artwork you want to find");

        Scanner scanner = new Scanner(System.in);

        Long inputID = Long.parseLong(scanner.nextLine());
        Artwork foundArtwork = em.find(Artwork.class, inputID);

        System.out.println(foundArtwork);

        // Assert
        assertThat(foundArtwork.getId()).isEqualTo(inputID);
    }

    @Test
    @DisplayName("Test method for finding an artwork and changing its location with user input to the Depot")
    void testMoveArtworkByUserToDepot() {
        // Arrange
        createSeveralArtWorks();
        Artwork artwork = queryWithId();
        Scanner scanner = new Scanner(System.in);
        Depot location = new Depot();

        // Act
        System.out.println("Do you want to move the artwork to the depot? Type 1 to proceed");
        int userInput = Integer.parseInt(scanner.nextLine());
        boolean moved = false;
        if (userInput == 1) {
            moved = artwork.moveTo(location);
            System.out.println(artwork.toString() + " was moved to " + location.toString());
            executeTransaction(em -> {
                em.persist(artwork);
                em.persist(location);
            });
        } else {
            System.out.println("Something went wrong, contact administrator or try again.");
        }

        //Assert
        assertThat(moved).isEqualTo(true);
        assertThat(location.getArtworks().get(0).getId()).isEqualTo(artwork.getId());
    }

    @Test
    @DisplayName("Test method for finding an artwork and changing its location with user input to either the Depot or " +
            "to the Zaal")
    void testMoveArtworkByUserToLocation() {
        // Arrange
        createSeveralArtWorks();
        Artwork artwork = queryWithId();
        Scanner scanner = new Scanner(System.in);
        Location depot = new Depot();
        Location zaal = new Zaal();
        Location locationInput = depot;

        // Act
        System.out.println("Type 1 to move the artwork to the depot, type 2 for moving it to the Zaal");
        int userInput = Integer.parseInt(scanner.nextLine());
        boolean moved = false;
        if (userInput == 1) {
            locationInput = depot;
            moved = artwork.moveTo(depot);
            System.out.println(artwork.toString() + " was moved to " + depot.toString());
        } else if (userInput == 2) {
            locationInput = zaal;
            moved = artwork.moveTo(zaal);
            System.out.println(artwork.toString() + " was moved to " + zaal.toString());

        } else {
            System.out.println("Something went wrong, contact administrator or try again.");
        }
        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(depot);
            em.persist(zaal);
        });

        //Assert
        assertThat(moved).isEqualTo(true);
        assertThat(locationInput.getArtworks().get(0).getId()).isEqualTo(artwork.getId());
    }

    @Test
    @DisplayName("Creating the museum method, requesting an update of where everything is and printing this to consul")
    void whereIsEverything() {
        // Arrange
        createMuseum();
        // Act
        List<Artwork> allArtworks = findAllArtworks();
        for (Artwork a: allArtworks) {
            System.out.println(a);
        }
        //Assert

    }

    @Test
    @DisplayName("Moving artwork from depot to zaal with user input")
    void moveArtWorkZaalToDepot() {
        // Arrange
        createMuseum();
        Scanner scanner = new Scanner(System.in);
        Artwork artwork = queryWithId();
        List<Location> locations = findAllLocations();
        // Act
        Location currentLocation = artwork.getLocation();

        if (currentLocation instanceof Depot) {
            System.out.println("Do you want to move " + artwork + "to the Zaal?");
        }


        System.out.println("Type 1 to move the artwork to the depot, type 2 for moving it to the Zaal");
        int userInput = Integer.parseInt(scanner.nextLine());
        boolean moved = false;
        if (userInput == 1) {
            moved = true;
            System.out.println(artwork.toString() + " was moved to " + );
        } else if (userInput == 2) {
            moved = true;
            System.out.println(artwork.toString() + " was moved to " + );

        } else {
            System.out.println("Something went wrong, contact administrator or try again.");
        }
        executeTransaction(em -> {
            em.persist(artwork);
        });

        //Assert
        assertThat(moved).isEqualTo(true);
    }


    private void createSeveralArtWorks() {
        Artist maxErnst = new Artist("Max Ernst", 1891, 1976, DADA);
        Artwork artwork1 = new Artwork("Europe after the Rain II", maxErnst, 1941);
        Artwork artwork2 = new Artwork("Blue Forest", maxErnst, 1931);
        Artwork artwork3 = new Artwork("Two Children Are Threatened by a Nightingale", maxErnst, 1924);
        Artist brancusi = new Artist("Constantin Brancusi", 1876, 1957, SURREALISM);
        Artwork artwork4 = new Artwork("The Fish", brancusi, 1924);

        executeTransaction(em -> {
            em.persist(maxErnst);
            em.persist(artwork1);
            em.persist(artwork2);
            em.persist(artwork3);
            em.persist(brancusi);
            em.persist(artwork4);
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

    public void createMuseum() {
        Location depot = new Depot();
        Location zaal = new Zaal();

        Artist maxErnst = new Artist("Max Ernst", 1891, 1976, DADA);
        Artwork artwork1 = new Artwork("Europe after the Rain II", maxErnst, 1941);
        Artwork artwork2 = new Artwork("Blue Forest", maxErnst, 1931);
        Artwork artwork3 = new Artwork("Two Children Are Threatened by a Nightingale", maxErnst, 1924);
        Artist brancusi = new Artist("Constantin Brancusi", 1876, 1957, SURREALISM);
        Artwork artwork4 = new Artwork("The Fish", brancusi, 1924);

        artwork1.moveTo(depot);
        artwork2.moveTo(depot);
        artwork3.moveTo(zaal);
        artwork4.moveTo(depot);

        executeTransaction(em -> {
            em.persist(maxErnst);
            em.persist(artwork1);
            em.persist(artwork2);
            em.persist(artwork3);
            em.persist(brancusi);
            em.persist(artwork4);
            em.persist(zaal);
            em.persist(depot);
        });

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

}
