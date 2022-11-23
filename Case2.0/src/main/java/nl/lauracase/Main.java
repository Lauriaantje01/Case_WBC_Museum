package nl.lauracase;

import nl.lauracase.controllers.MainController;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.inject.WeldInstance;

public class Main {


    public static void main (String args[]) {
        Weld weld = new Weld();
        WeldContainer cdiContainer = weld.initialize();
        WeldInstance<MainController> proxy = cdiContainer.select(MainController.class);
        System.out.println("Welcome to the pilot admin system of the Walter Bosch Complex museum\n");
        proxy.get().switchBoard();
    }
}
