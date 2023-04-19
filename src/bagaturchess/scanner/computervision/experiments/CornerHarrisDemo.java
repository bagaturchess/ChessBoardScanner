package bagaturchess.scanner.computervision.experiments;

import org.opencv.core.Core;


public class CornerHarrisDemo {
    public static void main(final String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CornerHarris(args);
            }
        });
    }
}
