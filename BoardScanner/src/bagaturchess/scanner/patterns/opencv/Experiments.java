package bagaturchess.scanner.patterns.opencv;


import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.opencv.OpenCVUtils.HoughLine;


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
		//Mat bilateralFilter = new Mat();
		//Imgproc.bilateralFilter(source_gray, bilateralFilter, 5, 150, 150);
		//source_gray = bilateralFilter;
		
        HighGui.imshow("source_gray", source_gray);
        HighGui.waitKey(0);
		
		Mat cannyOutput = new Mat();
		Imgproc.Canny(source_gray, cannyOutput, 20, 80);
		
        HighGui.imshow("cannyOutput", cannyOutput);
        HighGui.waitKey(0);
        
        ResultPair<List<HoughLine>, List<HoughLine>> all_lines = OpenCVUtils.getHoughTransform(cannyOutput, 1, Math.PI / 360, 120);
        List<HoughLine> h_lines = all_lines.getFirst();
        List<HoughLine> v_lines = all_lines.getSecond();
        
        List<HoughLine> lines = h_lines;
        
        //Mat toDraw = cannyOutput.clone();
        
        /*KMeansLines h_kmeans = new KMeansLines(5, h_lines);
        
        for (int centroid_id = 0; centroid_id < h_kmeans.weights.length; centroid_id++) {
        	
        	System.out.println("centroid_id=" + centroid_id + " lines count is " + h_kmeans.weights[centroid_id]);
        	
        	Mat toDraw = cannyOutput.clone();
        	for (int i = 0; i < h_kmeans.centroids_ids.length; i++) {
        		if (h_kmeans.centroids_ids[i] == centroid_id) {
        			HoughLine line = h_lines.get(i);
        			Imgproc.line(toDraw, line.pt1, line.pt2, new Scalar(255, 255, 255), 1);
        		}
        	}
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }*/
        
        //Point center = new Point(0, 0);
        //double[][] distanceToCenter = new double[h_lines.size()][h_lines.size()];
        Point[][] intersections = new Point[lines.size()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
        	for (int j = 0; j < lines.size(); j++) {
        		HoughLine line1 = lines.get(i);
        		HoughLine line2 = lines.get(j);
        		intersections[i][j] = OpenCVUtils.findIntersection(line1.pt1, line1.pt2, line2.pt1, line2.pt2);
        		/*if (intersections[i][j] != null) {
        			distanceToCenter[i][j] = Math.sqrt(Math.pow(intersections[i][j].x - center.x, 2) + Math.pow(intersections[i][j].y - center.y, 2));
        		} else {
        			distanceToCenter[i][j] = 0;
        		}*/
        	}
        }
        
        /*boolean[][] skipPoint = new boolean[h_lines.size()][h_lines.size()];
        for (int c = 0; c < 20; c++) {
        	
	        HoughLine line1 = null;
	        HoughLine line2 = null;
	        int index1 = -1;
	        int index2 = -1;
	        double biggestDistance = Double.MIN_VALUE;
	        for (int i = 0; i < distanceToCenter.length; i++) {
	        	for (int j = 0; j < distanceToCenter.length; j++) {
	        		if (!skipPoint[i][j]) {
	        			if (distanceToCenter[i][j] > biggestDistance) {
		        			biggestDistance = distanceToCenter[i][j];
		            		line1 = h_lines.get(i);
		            		line2 = h_lines.get(j);
		            		index1 = i;
		            		index2 = j;
		        		}
	        		}
	        	}
	        }
	        skipPoint[index1][index2] = true;
	        
	        System.out.println(biggestDistance);
	        
	        Imgproc.line(toDraw, line1.pt1, line1.pt2, new Scalar(255, 255, 255), 2);
	        Imgproc.line(toDraw, line2.pt1, line2.pt2, new Scalar(255, 255, 255), 2);
        }*/
        
        KMeansPoints h_kmeans = new KMeansPoints(5, intersections);
        
        //int centroid_id = h_kmeans.getFarthestCentroidIndex(new Point(0, 0));
        
        for (int centroid_id = 0; centroid_id < h_kmeans.weights.length; centroid_id++) {
        	
        	System.out.println("centroid_id=" + centroid_id + " points count is " + h_kmeans.weights[centroid_id] + ", centroid x y are " + h_kmeans.centroids_values[centroid_id]);
        	
        	Mat toDraw = cannyOutput.clone();
        	for (int i = 0; i < h_kmeans.centroids_ids.length; i++) {
        		for (int j = 0; j < h_kmeans.centroids_ids.length; j++) {
	        		if (h_kmeans.centroids_ids[i][j] == centroid_id) {
	        			HoughLine line1 = lines.get(i);
	        			HoughLine line2 = lines.get(j);
	        			Imgproc.line(toDraw, line1.pt1, line1.pt2, new Scalar(255, 255, 255), 2);
	        			Imgproc.line(toDraw, line2.pt1, line2.pt2, new Scalar(255, 255, 255), 2);
	        		}
        		}
        	}
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }
	}
}
