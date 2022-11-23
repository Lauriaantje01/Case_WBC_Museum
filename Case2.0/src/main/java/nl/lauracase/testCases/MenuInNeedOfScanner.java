package nl.lauracase.testCases;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Scanner;


public class MenuInNeedOfScanner {

    @Inject
    @ApplicationScoped
    Scanner scanner;

    @Inject
    Menu2InNeedOfScanner menu2;

    public void doSomething() {
        System.out.println("Type a number:");
        int input = Integer.parseInt(scanner.nextLine());
        System.out.println(input);
        System.out.println("Scanner received");
    }

    public boolean iHaveMenu2() {
        if(menu2.equals(null)) {
            return false;
        }
        else return true;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
