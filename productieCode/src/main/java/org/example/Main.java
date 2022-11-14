package org.example;

import org.example.menus.MainMenu;

public class Main {


    public static void main (String args[]) {
        MainMenu mainMenu = new MainMenu();
        System.out.println("Welcome to the pilot admin system of the Walter Bosch Complex museum\n");
        mainMenu.startMenu();
    }
}
