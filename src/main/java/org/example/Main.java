package org.example;

import Presentation.ClientView;
import Presentation.OrderView;
import Presentation.ProductView;

import javax.swing.*;

/**
 * Main class to launch the application.
 */
public class Main {

    /**
     * The main method that starts the application.
     * It creates and shows the client, product, and order views on the Swing event thread.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientView();
            new ProductView();
            new OrderView();
        });
    }
}
