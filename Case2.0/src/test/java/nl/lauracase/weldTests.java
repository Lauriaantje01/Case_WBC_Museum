package nl.lauracase;

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

        WeldInstance<createScanner> proxy = CDIcontainer.select(createScanner.class);
        createScanner createScanner = proxy.get();
        // Act
        Scanner scanner = createScanner.getScanner();

        //Assert
        assertThat(scanner).isNotNull();
    }
}
