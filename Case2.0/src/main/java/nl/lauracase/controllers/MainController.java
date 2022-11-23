package nl.lauracase.controllers;

import nl.lauracase.interfaces.Controller;
import nl.lauracase.services.InputServices;

import javax.inject.Inject;

public class MainController implements Controller {

    @Inject
    InputServices inputServices;

    @Override
    public void switchBoard() {
        boolean proceed = true;
        while (proceed) {
            viewOptions();
            int input = inputServices.validateMultipleChoiceUserInput(2);

            if (input == 1) {
                //collectionMenu.startCollectionMenu();
                System.out.println("\n\n\nOff to collection menu\n\n\n");
            } else if (input == 2) {
//                addArtworkMenu.startAddArtworkMenu();
                System.out.println("\n\n\nOff to add Artwork menu\n\n\n");
            } else {
                proceed = false;
            }
        }
    }

    @Override
    public void viewOptions() {
        System.out.println("1) Show and/or modify the current works in the collection\n" +
                "2) Add a new artwork to the collection.\n" +
                "0) Exit the program");
    }
}
