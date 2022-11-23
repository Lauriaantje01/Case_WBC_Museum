package nl.lauracase.testCases;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Scanner;

public class Menu2InNeedOfScanner {
    @Inject
    Scanner scanner;

    public Scanner getScanner() {
        return scanner;
    }
}
