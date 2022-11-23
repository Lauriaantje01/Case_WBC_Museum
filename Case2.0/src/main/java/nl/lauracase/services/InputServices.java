package nl.lauracase.services;

import javax.inject.Inject;
import java.util.Scanner;

public class InputServices {

    @Inject
    Scanner scanner;

    public int validateMultipleChoiceUserInput(int noOfOptions) {
        boolean proceed = true;
        int userInput = 0;

        while (proceed) {
            try {
                userInput = Integer.parseInt(scanner.nextLine());
                if (userInput > 0 || userInput <= noOfOptions) {
                    return userInput;
                } else if (userInput == 0) {
                    return 0;
                } else System.out.println("Try again typing the number of one of the options");
            } catch (NumberFormatException e) {
                System.out.println("Try again typing the number of one of the options");
            } catch (NullPointerException e) {
                System.out.println("Looks like something went wrong with the scanner");
            }
        }
        return userInput;
    }
}
