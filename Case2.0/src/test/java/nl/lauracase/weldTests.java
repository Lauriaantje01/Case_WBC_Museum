package nl.lauracase;

import nl.lauracase.testCases.MenuInNeedOfScanner;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.inject.WeldInstance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class weldTests {

    @Test
    @DisplayName("Test if a weld instance can be created for a scanner class")
    void testScannerWeldInstance() {
        // Arrange
        Weld weld = new Weld();
        WeldContainer CDIcontainer = weld.initialize();

        WeldInstance<MenuInNeedOfScanner> proxy = CDIcontainer.select(MenuInNeedOfScanner.class);
        MenuInNeedOfScanner menuInNeedOfScanner = proxy.get();
        // Act
        menuInNeedOfScanner.doSomething();
    }

    @Test
    @DisplayName("Test if a weld instance can be created for a scanner class, only one scanner created?")
    void testScannersWeldInstance() {
        // Arrange
        Weld weld = new Weld();
        WeldContainer CDIcontainer = weld.initialize();

        WeldInstance<MenuInNeedOfScanner> proxy = CDIcontainer.select(MenuInNeedOfScanner.class);
        MenuInNeedOfScanner menuInNeedOfScanner = proxy.get();
        Scanner scanner1 = menuInNeedOfScanner.getScanner();

        boolean hasMenu2 = menuInNeedOfScanner.iHaveMenu2();

        assertThat(scanner1).isNotNull();
        assertThat(hasMenu2).isEqualTo(true);
    }

}
