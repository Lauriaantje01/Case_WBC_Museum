package org.example;

import org.example.menus.MainMenu;

public class Main {

    public static void main (String args[]) {
        MainMenu mainMenu = new MainMenu();
        System.out.println("Welcome to the pilot of the Walter Bosch Complex museum admin system. \n\n");
        mainMenu.startMenu();
    }
}
