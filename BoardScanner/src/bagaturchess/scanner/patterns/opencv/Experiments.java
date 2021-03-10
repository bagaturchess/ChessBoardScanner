package bagaturchess.scanner.patterns.opencv;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
		
        //HighGui.imshow("source_gray", source_gray);
        //HighGui.waitKey(0);
		
		Mat cannyOutput = new Mat();
		Imgproc.Canny(source_gray, cannyOutput, 20, 80);
		
        HighGui.imshow("cannyOutput", cannyOutput);
        HighGui.waitKey(0);
        
        ResultPair<List<HoughLine>, List<HoughLine>> all_lines = OpenCVUtils.getHoughTransform(cannyOutput, 1, Math.PI / 360, 120);
        List<HoughLine> h_lines = all_lines.getFirst();
        List<HoughLine> v_lines = all_lines.getSecond();
        
        int x_test1 = 0;
        int x_test2 = source_gray.width();
        List<Hough9Lines> hough9Lines_H_x1 = genHough9Lines_Horizontal(source_gray.height() / 20, source_gray.height() / 5, x_test1, h_lines);
        hough9Lines_H_x1 = correctErrorWithSecondPointX(hough9Lines_H_x1, x_test2);
        
        //List<Hough9Lines> hough9Lines_H_x2 = genHough9Lines_Horizontal(source_gray.height() / 20, source_gray.height() / 5, x_test2, h_lines);
        
        for (int i = 0; i < hough9Lines_H_x1.size(); i++) {
        	
        	Hough9Lines lines = hough9Lines_H_x1.get(i);
        	
        	Mat toDraw = cannyOutput.clone();
        	
        	Imgproc.line(toDraw, lines.lines.get(0).pt1, lines.lines.get(0).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(1).pt1, lines.lines.get(1).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(2).pt1, lines.lines.get(2).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(3).pt1, lines.lines.get(3).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(4).pt1, lines.lines.get(4).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(5).pt1, lines.lines.get(5).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(6).pt1, lines.lines.get(6).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(7).pt1, lines.lines.get(7).pt2, new Scalar(255, 255, 255), 2);
        	Imgproc.line(toDraw, lines.lines.get(8).pt1, lines.lines.get(8).pt2, new Scalar(255, 255, 255), 2);
        	
        	System.out.println("error is " + lines.error);
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }
        
        /*double[] x_0 = new double[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
        	x_0[i] = lines.get(i).calculateY(0);
        }
        
        KMeansLines_Scalar kmeans = new KMeansLines_Scalar(9, x_0);
        for (int centroid_id = 0; centroid_id < kmeans.weights.length; centroid_id++) {
        	
        	Mat toDraw = cannyOutput.clone();
        	
        	for (int i = 0; i < lines.size(); i++) {
        		if (centroid_id == kmeans.centroids_ids[i]) {
        			HoughLine line = lines.get(i);
        			Imgproc.line(toDraw, line.pt1, line.pt2, new Scalar(255, 255, 255), 2);
        		}
        	}
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }*/
        
        /*double[][] distancesOfYInX0 = new double[lines.size()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
        	for (int j = 0; j < lines.size(); j++) {
        		HoughLine line1 = lines.get(i);
        		HoughLine line2 = lines.get(j);
        		distancesOfYInX0[i][j] = Math.abs(line1.calculateY(0) - line2.calculateY(0));
        	}
        }
        
        KMeansLines_Distances distances = new KMeansLines_Distances(5, distancesOfYInX0);
        
        for (int centroid_id = 0; centroid_id < distances.weights.length; centroid_id++) {
        	
        	System.out.println("centroid_id=" + centroid_id + " distances count is " + distances.weights[centroid_id] + ", centroid distance is " + distances.centroids_values[centroid_id]);
        	
        	Mat toDraw = cannyOutput.clone();
            for (int i = 0; i < lines.size(); i++) {
            	for (int j = 0; j < lines.size(); j++) {
        			
        			if (i == j) {
        				continue;
        			}
        			
	        		if (distances.centroids_ids[i][j] == centroid_id) {
	        			HoughLine line1 = lines.get(i);
	        			HoughLine line2 = lines.get(j);
	        			Imgproc.line(toDraw, line1.pt1, line1.pt2, new Scalar(255, 255, 255), 2);
	        			Imgproc.line(toDraw, line2.pt1, line2.pt2, new Scalar(255, 255, 255), 2);
	        		}
        		}
        	}
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }*/
        
        //Mat toDraw = cannyOutput.clone();
        
        /*KMeansLines_Theta h_kmeans = new KMeansLines_Theta(5, lines);
        
        for (int centroid_id = 0; centroid_id < h_kmeans.weights.length; centroid_id++) {
        	
        	System.out.println("centroid_id=" + centroid_id + " lines count is " + h_kmeans.weights[centroid_id]);
        	
        	Mat toDraw = cannyOutput.clone();
        	for (int i = 0; i < h_kmeans.centroids_ids.length; i++) {
        		if (h_kmeans.centroids_ids[i] == centroid_id) {
        			HoughLine line = lines.get(i);
        			Imgproc.line(toDraw, line.pt1, line.pt2, new Scalar(255, 255, 255), 2);
        		}
        	}
        	
            HighGui.imshow("lines", toDraw);
            HighGui.waitKey(0);
        }*/
        
        /*Point[][] intersections = new Point[lines.size()][lines.size()];
        for (int i = 0; i < lines.size(); i++) {
        	for (int j = 0; j < lines.size(); j++) {
        		HoughLine line1 = lines.get(i);
        		HoughLine line2 = lines.get(j);
        		intersections[i][j] = OpenCVUtils.findIntersection(line1.pt1, line1.pt2, line2.pt1, line2.pt2);
        	}
        }*/
        
        /*
        int maxCountOfParallels = -1;
        int maxCountOfParallelsIndex = -1;
        for (int i = 0; i < intersections.length; i++) {
        	int countOfNulls = 0;
        	for (int j = 0; j < intersections.length; j++) {
        		if (i != j) {
        			Point point = intersections[i][j];
        			if (point == null) {
        				countOfNulls++;
        			}
        		}
        	}
        	if (countOfNulls > maxCountOfParallels) {
        		maxCountOfParallels = countOfNulls;
        		maxCountOfParallelsIndex = i;
        	}
        }
        System.out.println(maxCountOfParallels);
        
        Mat toDraw = cannyOutput.clone();
    	for (int j = 0; j < intersections.length; j++) {
    		if (maxCountOfParallelsIndex != j) {
    			Point point = intersections[maxCountOfParallelsIndex][j];
    			if (point == null) {
        			HoughLine line1 = lines.get(maxCountOfParallelsIndex);
        			HoughLine line2 = lines.get(j);
        			Imgproc.line(toDraw, line1.pt1, line1.pt2, new Scalar(255, 255, 255), 2);
        			Imgproc.line(toDraw, line2.pt1, line2.pt2, new Scalar(255, 255, 255), 2);
    			}
    		}
    	}
        
        HighGui.imshow("lines", toDraw);
        HighGui.waitKey(0);*/
        
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
        
        /*KMeansPoints h_kmeans = new KMeansPoints(10, intersections);
        
        //int centroid_id = h_kmeans.getFarthestCentroidIndex(new Point(0, 0));
        
        for (int centroid_id = 0; centroid_id < h_kmeans.weights.length; centroid_id++) {
        	
        	System.out.println("centroid_id=" + centroid_id + " points count is " + h_kmeans.weights[centroid_id] + ", centroid x y are " + h_kmeans.centroids_values[centroid_id]);
        	
        	Mat toDraw = cannyOutput.clone();
            for (int i = 0; i < lines.size(); i++) {
            	for (int j = 0; j < lines.size(); j++) {
            		
            		if (i == j) {
            			continue;
            		}
            		
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
        }*/
	}


	private static List<Hough9Lines> genHough9Lines_Horizontal(int start_interval, int end_interval, int x_test, List<HoughLine> lines) {
		
		lines = sortByYInX(lines, x_test);
		
		List<Hough9Lines> result = new ArrayList<Hough9Lines>();
        for (int i = 0; i < 0.15d * lines.size(); i++) {
        	for (int interval = start_interval; interval < end_interval; interval++) {
        		
        		int line1Index = i;
        		HoughLine line1 = lines.get(line1Index);
        		double y1 = line1.calculateY(x_test);
        		double error1 = 0;
        		
        		int line2Index = findClosestLineIndexByX(line1Index + 1, lines, x_test, y1 + 1 * interval);
        		if (line2Index == -1) continue;
        		HoughLine line2 = lines.get(line2Index);
        		double error2 = Math.abs(line2.calculateY(x_test) - (y1 + 1 * interval));
        		
        		int line3Index = findClosestLineIndexByX(line2Index + 1, lines, x_test, y1 + 2 * interval);
        		if (line3Index == -1) continue;
        		HoughLine line3 = lines.get(line3Index);
        		double error3 = Math.abs(line3.calculateY(x_test) - (y1 + 2 * interval));
        		
        		int line4Index = findClosestLineIndexByX(line3Index + 1, lines, x_test, y1 + 3 * interval);
        		if (line4Index == -1) continue;
        		HoughLine line4 = lines.get(line4Index);
        		double error4 = Math.abs(line4.calculateY(x_test) - (y1 + 3 * interval));
        		
        		int line5Index = findClosestLineIndexByX(line4Index + 1, lines, x_test, y1 + 4 * interval);
        		if (line5Index == -1) continue;
        		HoughLine line5 = lines.get(line5Index);
        		double error5 = Math.abs(line5.calculateY(x_test) - (y1 + 4 * interval));
        		
        		int line6Index = findClosestLineIndexByX(line5Index + 1, lines, x_test, y1 + 5 * interval);
        		if (line6Index == -1) continue;
        		HoughLine line6 = lines.get(line6Index);
        		double error6 = Math.abs(line6.calculateY(x_test) - (y1 + 5 * interval));
        		
        		int line7Index = findClosestLineIndexByX(line6Index + 1, lines, x_test, y1 + 6 * interval);
        		if (line7Index == -1) continue;
        		HoughLine line7 = lines.get(line7Index);
        		double error7 = Math.abs(line7.calculateY(x_test) - (y1 + 6 * interval));
        		
        		int line8Index = findClosestLineIndexByX(line7Index + 1, lines, x_test, y1 + 7 * interval);
        		if (line8Index == -1) continue;
        		HoughLine line8 = lines.get(line8Index);
        		double error8 = Math.abs(line8.calculateY(x_test) - (y1 + 7 * interval));
        		
        		int line9Index = findClosestLineIndexByX(line8Index + 1, lines, x_test, y1 + 8 * interval);
        		if (line9Index == -1) continue;
        		HoughLine line9 = lines.get(line9Index);
        		double error9 = Math.abs(line9.calculateY(x_test) - (y1 + 8 * interval));
        		
        		double error_all = error1 + error2 + error3 + error4 + error5 + error6 + error7 + error8 + error9;
        		
        		List<HoughLine> linesList = new ArrayList<HoughLine>();
        		linesList.add(line1);
        		linesList.add(line2);
        		linesList.add(line3);
        		linesList.add(line4);
        		linesList.add(line5);
        		linesList.add(line6);
        		linesList.add(line7);
        		linesList.add(line8);
        		linesList.add(line9);
        		
        		Hough9Lines entry = new Hough9Lines(linesList, error_all);
        		result.add(entry);
        	}
        }
        
        return sortByError(result);
	}
	
	
	private static List<Hough9Lines> correctErrorWithSecondPointX(List<Hough9Lines> hough9Lines, int x_test) {
        
		for (int i = 0; i < hough9Lines.size(); i++) {
        	
        	Hough9Lines lines9 = hough9Lines.get(i);
        	double minY = Double.MAX_VALUE;
        	double maxY = Double.MIN_VALUE;
        	for (int j = 0; j < lines9.lines.size(); j++) {
        		HoughLine line = lines9.lines.get(j);
        		double y2 = line.calculateY(x_test);
        		if (y2 < minY) {
        			minY = y2;
        		}
        		if (y2 > maxY) {
        			maxY = y2;
        		}
        	}
        	
        	double errorCorrection = 0;
        	for (int j = 0; j < lines9.lines.size(); j++) {
        		HoughLine line = lines9.lines.get(j);
        		double expectedY = minY + ((maxY - minY) * j) / (double) (lines9.lines.size() - 1);
        		errorCorrection += Math.abs(expectedY - line.calculateY(x_test));
        	}
        	
        	lines9.error += errorCorrection;
        }
		
		return sortByError(hough9Lines);
	}
	
	
	private static List<Hough9Lines> sortByError(List<Hough9Lines> result) {
		
		Hough9Lines[] array = result.toArray(new Hough9Lines[result.size()]);
        
		Arrays.sort(array, new Comparator<Hough9Lines>() {
			@Override
			public int compare(Hough9Lines l1, Hough9Lines l2) {
				double y1 = l1.error;
				double y2 = l2.error;
				if (y1 > y2) {
					return 1;
				} else if (y1 < y2) {
					return -1;
				} else {
					return -1;
				}
			}
		});
		
		return Arrays.asList(array);
	}
	
	
	private static int findClosestLineIndexByX(int startIndex, List<HoughLine> lines, double x_test, double y_test) {
		
		int bestMatchIndex = -1;
		double bestMatchDelta = Double.MAX_VALUE;
		
		for (int i = startIndex; i < lines.size(); i++) {
			HoughLine line = lines.get(i);
			double delta = Math.abs(line.calculateY(x_test) - y_test);
			if (delta < bestMatchDelta) {
				bestMatchDelta = delta;
				bestMatchIndex = i;
			}
		}
		
		return bestMatchIndex;
	}


	private static List<HoughLine> sortByYInX(List<HoughLine> lines, final int x) {
		
		HoughLine[] array = lines.toArray(new HoughLine[lines.size()]);
		
		Arrays.sort(array, new Comparator<HoughLine>() {
			@Override
			public int compare(HoughLine l1, HoughLine l2) {
				double y1 = l1.calculateY(x);
				double y2 = l2.calculateY(x);
				if (y1 > y2) {
					return 1;
				} else if (y1 < y2) {
					return -1;
				} else {
					return -1;
				}
			}
		});
		
		return Arrays.asList(array);
	}
	
	
	private static class Hough9Lines {
		
		
		private List<HoughLine> lines;
		private double error;
		
		
		public Hough9Lines(List<HoughLine> _lines, double _error) {
			lines = _lines;
			error = _error;
		}
	}
}
