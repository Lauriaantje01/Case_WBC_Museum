package nl.lauracase;

import jakarta.inject.Inject;

import java.util.Scanner;

public class createScanner {

    @Inject
    Scanner scanner;

    public Scanner getScanner() {
        System.out.println("Hier is je scanner");
        return scanner;
    }
}
