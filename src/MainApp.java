import unitrack.view.MainFrame;

import javax.swing.*;

/**
 * Step 38: Application Entry Point
 *
 * Bootstraps the UniTrack Smart Academic Calendar desktop application.
 *
 * Key rules respected:
 *   1. All Swing UI creation is dispatched onto the Event Dispatch Thread (EDT)
 *      via SwingUtilities.invokeLater — preventing race conditions on startup.
 *   2. No logic lives in main() beyond this dispatch — keeps the entry point lean.
 */
public class MainApp {
    public static void main(String[] args) {
        // Rule: ALL Swing operations must happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
