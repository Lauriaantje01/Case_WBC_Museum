package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.Styles.DADA;
import static org.example.Styles.SURREALISM;

public class testUpdateEntity {

    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();


    @Test
    @DisplayName("Bouwen van een functie waarbij je een kunstwerk kan updaten. Hier probeer ik een jaar te veranderen" +
            "van een kusntwerk dat al in de database staat")
    void updateEntityArtwork() {
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

}
