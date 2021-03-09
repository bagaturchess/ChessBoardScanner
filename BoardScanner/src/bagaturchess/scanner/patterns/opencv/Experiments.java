package bagaturchess.scanner.patterns.opencv;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;


public class Experiments {
	
	
	public static void tryit(Mat source_rgb) {
		
		Mat source_gray = new Mat(source_rgb.height(), source_rgb.width(), CvType.CV_8UC4);
		Imgproc.cvtColor(source_rgb, source_gray, Imgproc.COLOR_BGR2GRAY);
		
		//to check:
		//https://github.com/andrewleeunderwood/project_MYM/blob/master/cv_chess.py
		//https://github.com/andrewleeunderwood/project_MYM/blob/master/cv_chess_functions.py
		
		/*ORB featureDetector = ORB.create();
		MSER detector = MSER.create();
		
		//Image 1
		Mat source_gray1 = new Mat(source_rgb.height(), source_rgb.width(), CvType.CV_8UC4);
		Imgproc.cvtColor(source_rgb, source_gray1, Imgproc.COLOR_BGR2GRAY);
		MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
		Mat descriptors1 = new Mat();
		detector.detect(source_gray1, keypoints1);
		featureDetector.compute(source_gray1, keypoints1, descriptors1);
		
		//Image 2
		Mat source_gray2 = new Mat(targetPerspective.height(), targetPerspective.width(), CvType.CV_8UC4);
		Imgproc.cvtColor(targetPerspective, source_gray2, Imgproc.COLOR_BGR2GRAY);
		MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
		Mat descriptors2 = new Mat();
		detector.detect(source_gray2, keypoints2);
		featureDetector.compute(source_gray2, keypoints2, descriptors2);
		*/
		
		// DRAWING OUTPUT
		//Mat outputImg = new Mat();
		// this will draw all matches, works fine
		//System.out.println(keypoints.size());
		//Features2d.drawKeypoints(source_gray, keypoints, outputImg);

        //HighGui.imshow("Key points", outputImg);
        //HighGui.waitKey(0);
        
        
		/*DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
		List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
		matcher.knnMatch(descriptors1, descriptors2, matches, 1);
		
		// DRAWING OUTPUT
		for (MatOfDMatch match: matches) {
			// this will draw all matches, works fine
			Mat outputImg = new Mat();
			Features2d.drawMatches(source_gray1, keypoints1, source_gray2, keypoints2, match, outputImg);
	        HighGui.imshow("Key points", outputImg);
	        HighGui.waitKey(0);
		}*/
		
		//A LBP opencv classifier for chessboard detection
		/*CascadeClassifier classifier = new CascadeClassifier("./res/LBP.chessboard.classifier.xml");
		MatOfRect board = new MatOfRect();
		classifier.detectMultiScale(source_gray, board);
		System.out.println(String.format("Detected %s boards", board.toArray().length));
		
		Imgproc.rectangle(source_gray, board.toArray()[0], new Scalar(255, 255, 255));
		
        HighGui.imshow("Key points", source_gray);
        HighGui.waitKey(0);
		*/
		
		Imgproc.GaussianBlur(source_gray, source_gray, new Size(15,15), 0.5);
		//Imgproc.adaptiveThreshold(source_gray, source_gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
        //Imgproc.threshold(source_gray, source_gray, 10, 255, Imgproc.THRESH_BINARY);
		
        HighGui.imshow("source_gray", source_gray);
        HighGui.waitKey(0);
		
		Mat cannyOutput = new Mat();
		Imgproc.Canny(source_gray, cannyOutput, 20, 80);
		
        HighGui.imshow("cannyOutput", cannyOutput);
        HighGui.waitKey(0);
        
        Mat lines = OpenCVUtils.getHoughTransform(cannyOutput, 1, Math.PI / 360, 120);
        
        HighGui.imshow("lines", lines);
        HighGui.waitKey(0);
        
        //Core.kmeans(data, K, bestLabels, criteria, attempts, flags);
	}
}
