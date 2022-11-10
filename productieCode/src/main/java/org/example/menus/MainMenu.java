package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class MainMenu {
    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();

    AddArtworkMenu addArtworkMenu = new AddArtworkMenu();
    Scanner scanner = new Scanner(System.in);

    public MainMenu() {
    }

    public void startMenu() {
        createMuseum();
        System.out.println("Welcome to the pilot of the Walter Bosch Complex museum admin system. \n\n");

        int input;
        boolean proceed = true;

        while (proceed) {
            System.out.println("Type 1 if you would like to have a look and/or modify the current works in the collection? \n" +
                    "Type 2 if you want to add a new artwork to the collection.\n" +
                    "Type 3 if you want to exit the program");
            input = Integer.parseInt(scanner.nextLine());

            if (input == 1) {
                System.out.println("move to current collection menu");
            } else if (input == 2) {
                addArtworkMenu.startAddArtworkMenu();
            } else if (input == 3) {
                proceed = false;
            } else System.out.println("Try again typing either 1, 2 or 3");
        }

        System.out.println("goodbye!");
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

    private Location getDepot() {
        String locationQuery = "SELECT l from Location l WHERE l.name = 'Depot'";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        return jpqlQueryLocation.getSingleResult();
    }

    private List<Location> findAllLocations() {
        String locationQuery = "SELECT l from Location l";
        TypedQuery<Location> jpqlQueryLocation = em.createQuery(locationQuery, Location.class);
        return jpqlQueryLocation.getResultList();
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
