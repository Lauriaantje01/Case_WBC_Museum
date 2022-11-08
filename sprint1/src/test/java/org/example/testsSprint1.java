package org.example;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Styles.*;

public class testsSprint1 {
    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();

    @Test
    @DisplayName("creating object/entity Artist")
    void createArtistWithArtWork() {
        Artist maxErnst = new Artist("Max Ernst", 1891, 1976, DADA);
        Artwork artwork = new Artwork("Europe after the Rain II", maxErnst, 1941);

        System.out.println(maxErnst.getArtworks());
        assertThat(artwork.getArtist()).isEqualTo(maxErnst);
    }

    @Test
    @DisplayName("Adding artist + artwork to the database. Both objects are now entities and have a one to many relation")
    void addArtistArtworkToDatabase() {
        Artist maxErnst = new Artist("Max Ernst", 1891, 1976, DADA);
        Artwork artwork = new Artwork("Europe after the Rain II", maxErnst, 1941);

        executeTransaction(em -> {
            em.persist(artwork);
            em.persist(maxErnst);
        });
    }

    @Test
    @DisplayName("Putting an art object in a certain location")
    void placeArtWorkInDepot() {
        addArtistArtworkToDatabase();

        String queryFindArtwork = "SELECT a from Artwork a where a.year = 1941";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindArtwork, Artwork.class);
        Artwork artwork = psqlQuery.getSingleResult();

        Depot depot = new Depot();
        boolean moveSuccess = artwork.moveTo(depot);
        System.out.println(depot.getArtworks().get(0));

        assertThat(moveSuccess).isEqualTo(true);
        assertThat(depot.getArtworks().get(0)).isEqualTo(artwork);
    }

    @Test
    @DisplayName("Creating multiple artworks and artists and adding these to the database")
    void addingMultipleArtWorksAndArtists() {
        createSeveralArtWorks();
        String queryFindArtwork = "SELECT a from Artwork a where a.artist.name = 'Max Ernst'";
        TypedQuery<Artwork> psqlQuery = em.createQuery(queryFindArtwork, Artwork.class);
        List<Artwork> artworks = psqlQuery.getResultList();

        for (Artwork a : artworks) {
            System.out.println(a);
        }

        assertThat(artworks.get(0).getArtist().getName()).isEqualTo("Max Ernst");
    }

    @Test
    @DisplayName("Creating a simple consul in which user could give a name (String object) which is stored")
    void testConsulInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a name");

        String input = scanner.nextLine();

        System.out.println("you wrote:  " + input);
    }
    @Test
    @DisplayName("Creating a simple consul in which user creates an artwork which is updated to the database")
    void testConsulInputArtist() {
        //Arrange
        Scanner scanner = new Scanner(System.in);

        //Act
        System.out.println("Type the title of the artwork");
        String inputTitle = scanner.nextLine();
        System.out.println("What is the year of the artwork?");
        int inputYear = Integer.parseInt(scanner.nextLine());
        System.out.println(inputTitle + " created in " + inputYear +". What is the name of the artist?");
        String inputArtistName = scanner.nextLine();
        Artist artist = new Artist (inputArtistName);

        Artwork artwork= new Artwork(inputTitle, artist, inputYear);

        System.out.println("The following artwork has been created: " + artwork.toString() + "Type 1 to proceed");
        String answerProceed = scanner.nextLine();
        if (answerProceed.equals("1")) {
            executeTransaction(em -> {
                em.persist(artist);
                em.persist(artwork);
            });
            System.out.println("You added the following object to the database: \n" +
                    artwork.toString());
        }
        else System.out.println("Mission aborted");


        String query = "SELECT a From Artwork a";
        TypedQuery<Artwork> jpqlQuery = em.createQuery(query, Artwork.class);
        Artwork artworkDB = jpqlQuery.getSingleResult();

        // Assert
        assertThat(artworkDB.getTitle()).isEqualTo(inputTitle);
    }

    @Test
    @DisplayName("Calling in the seperate method of creating an artwork, than double checking if this object has been added" +
            "to the database")
    void testIfConsulAddsToDataBase() {

        //Arrange
        addArtWorkConsul();
        String query = "SELECT a From Artwork a";
        TypedQuery<Artwork> jpqlQuery = em.createQuery(query, Artwork.class);
        Artwork artwork = jpqlQuery.getSingleResult();

        //Act

        //Assert

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

    private void addArtWorkConsul() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type the title of the artwork");
        String inputTitle = scanner.nextLine();

        System.out.println("What is the year of the artwork?");
        int inputYear = Integer.parseInt(scanner.nextLine());

        System.out.println(inputTitle + " created in " + inputYear +". What is the name of the artist?");
        String inputArtistName = scanner.nextLine();

        Artist artist = new Artist (inputArtistName);
        Artwork artwork= new Artwork(inputTitle, artist, inputYear);

        System.out.println("The following artwork has been created: " + artwork.toString() + "Type 1 to proceed");
        String answerProceed = scanner.nextLine();
        if (answerProceed.equals("1")) {
            executeTransaction(em -> {
                em.persist(artist);
                em.persist(artwork);
            });
            System.out.println("You added the following object to the database: \n" +
                    artwork.toString());
        }
        else System.out.println("Mission aborted");
    }
}
