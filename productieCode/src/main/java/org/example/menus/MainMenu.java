package org.example.menus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.Scanner;
import java.util.function.Consumer;

public class MainMenu {
    String persistenceUnitName = "jpa-hiber-postgres-pu";
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnitName);
    EntityManager em = emf.createEntityManager();
    Scanner scanner = new Scanner(System.in);

    public MainMenu() {
    }

    public void startMenu() {
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
            }
            else if (input ==2) {
                System.out.println("move to add artwork menu");
            }
            else if (input ==3) {
                proceed = false;
            }
            else System.out.println("Try again typing either 1, 2 or 3");
        }

        System.out.println("goodbye!");
    }

    private void addArtworkMenu() {
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
