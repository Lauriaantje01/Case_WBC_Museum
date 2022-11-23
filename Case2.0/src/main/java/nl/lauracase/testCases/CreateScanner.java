package nl.lauracase.testCases;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Scanner;


public class CreateScanner {
    @Produces
    Scanner scanner = new Scanner(System.in);


//    @ApplicationScoped
    public Scanner getScanner() {
        System.out.println("Hier is je scanner");
        return scanner;
    }
}
