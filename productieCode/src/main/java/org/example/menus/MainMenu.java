package org.example.menus;

import org.example.models.example.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/*
The main menu serves as a switch board to the other two submenus: interacting with the current collection or adding
a new artwork to the collection. The emf is passed on to the other submenus.

The menu sets up the Entity Manager Factory and creates the main museum objects. The createMuseum method, as well as
the executeTransaction method can be removed once there is already a filled database at the start of the program.

 */

public class MainMenu {
    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();
    AddArtworkMenu addArtworkMenu = new AddArtworkMenu(emf);

    CollectionMenu collectionMenu = new CollectionMenu(emf);
    Scanner scanner = new Scanner(System.in);

    public MainMenu() {
    }

    public void startMenu() {
        createMuseum();
        em.close(); // Presume that from now one the menu does not need to interact with the museum objects anymore.
        System.out.println("Type the number of one of the following options to continue:");

        boolean proceed = true;

        while (proceed) {
            System.out.println("1) Show and/or modify the current works in the collection\n" +
                    "2) Add a new artwork to the collection.\n" +
                    "0) Exit the program");
            String input = scanner.nextLine();

            if (input.equals("1")) {
                collectionMenu.startCollectionMenu();

            } else if (input.equals("2")) {
                addArtworkMenu.startAddArtworkMenu();

            } else if (input.equals("0")) {
                proceed = false;

            } else System.out.println("Try again typing either 1, 2 or 0");
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

}
