package bagaturchess.scanner.patterns.opencv.experiments;

import org.opencv.core.Core;

public class MorphologyDemo {
    public static void main(String[] args) {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Morphology().run(new String[] {"./res/cnn/lichess.org/set1/pictures/test7.png"});
    }
}