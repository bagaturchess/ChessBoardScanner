package bagaturchess.scanner.computervision.experiments;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

class CornerHarris {
    private Mat srcGray = new Mat();
    private Mat dst = new Mat();
    private Mat dstNorm = new Mat();
    private Mat dstNormScaled = new Mat();
    private JFrame frame;
    private JLabel imgLabel;
    private JLabel cornerLabel;
    private static final int MAX_THRESHOLD = 255;
    private int threshold = 200;
    
    public CornerHarris(String[] args) {
        String filename = args.length > 0 ? args[0] : "./data/tests/preprocess/test30.png";
        Mat src = Imgcodecs.imread(filename);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        // Create and set up the window.
        frame = new JFrame("Harris corner detector demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }
    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        sliderPanel.add(new JLabel("Threshold: "));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, threshold);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                threshold = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);
        JPanel imgPanel = new JPanel();
        imgLabel = new JLabel(new ImageIcon(img));
        imgPanel.add(imgLabel);
        Mat blackImg = Mat.zeros(srcGray.size(), CvType.CV_8U);
        cornerLabel = new JLabel(new ImageIcon(HighGui.toBufferedImage(blackImg)));
        imgPanel.add(cornerLabel);
        pane.add(imgPanel, BorderLayout.CENTER);
    }
    private void update() {
        dst = Mat.zeros(srcGray.size(), CvType.CV_32F);
        int blockSize = 2;
        int apertureSize = 3;
        double k = 0.04;
        Imgproc.cornerHarris(srcGray, dst, blockSize, apertureSize, k);
        Core.normalize(dst, dstNorm, 0, 255, Core.NORM_MINMAX);
        Core.convertScaleAbs(dstNorm, dstNormScaled);
        float[] dstNormData = new float[(int) (dstNorm.total() * dstNorm.channels())];
        dstNorm.get(0, 0, dstNormData);
        for (int i = 0; i < dstNorm.rows(); i++) {
            for (int j = 0; j < dstNorm.cols(); j++) {
                if ((int) dstNormData[i * dstNorm.cols() + j] > threshold) {
                    Imgproc.circle(dstNormScaled, new Point(j, i), 5, new Scalar(0), 2, 8, 0);
                }
            }
        }
        cornerLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(dstNormScaled)));
        frame.repaint();
    }
}