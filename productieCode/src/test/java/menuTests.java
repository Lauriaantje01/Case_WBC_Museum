import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class menuTests {

    @Test
    void startMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the pilot of the Walter Bosch Complex museum admin system. Continue?");
        String input = scanner.nextLine();
        boolean proceed = true;

        while (proceed) {
            if (input.equals("yes")) {
                System.out.println("Hello, proceed?");
                input = scanner.nextLine();
            }
            if (input.equals("no"))
                proceed = false;
        }
    }
}
