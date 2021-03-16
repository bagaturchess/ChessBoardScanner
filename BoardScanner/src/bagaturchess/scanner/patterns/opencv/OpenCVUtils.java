/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.patterns.opencv;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import bagaturchess.scanner.common.KMeansScalar;
import bagaturchess.scanner.common.ResultPair;


public class OpenCVUtils {
	
	
	public static void extendRect( Rect rect, double percent) {
        double heightExtension = percent * rect.height;
        double widthExtension = percent * rect.width;
        rect.x -= widthExtension / 2;
        rect.width += widthExtension;
        rect.y -= heightExtension / 2;
        rect.height += heightExtension;
	}
	
	
	public static Point[] getMinimalQuadrilateral(Point[] convexPolygon, Rect boundingRec) {
		
		if (convexPolygon.length <= 4) {
			throw new IllegalStateException();
		}
		
		//Create list with all entries
		List<ListItem<Point>> all_init_list = new ArrayList<ListItem<Point>>();
		for (int i = 0; i < convexPolygon.length; i++) {
			ListItem<Point> cur = new ListItem<Point>();
			cur.value = convexPolygon[i];
			all_init_list.add(cur);
		}
		
		//Link the list
		for (int i = 0; i < all_init_list.size() - 1; i++) {
			all_init_list.get(i).next = all_init_list.get(i + 1);
		}
		//Make it cyclic
		all_init_list.get(all_init_list.size() - 1).next = all_init_list.get(0);
		
		
		int countOfPoints = all_init_list.size();
		ListItem<Point> start = all_init_list.get(0);
		
		while (countOfPoints > 4) {
			
			//System.out.println("countOfPoints=" + countOfPoints);
			
			double minTriangleArea = Double.MAX_VALUE;
			ListItem<Point> best = null;
			ListItem<Point> best_intersection = new ListItem<Point>();
			ListItem<Point> cur = start;
			do {
				Point p1 = cur.value;
				Point p2 = cur.next.value;
				Point p3 = cur.next.next.value;
				Point p4 = cur.next.next.next.value;
				
				//Do work
				Point intersection = findIntersection(p1, p2, p4, p3);
				if (intersection != null && boundingRec.contains(intersection)) {
					double cur_area = triangleArea(p2, intersection, p3);
					if (cur_area < minTriangleArea) {
						minTriangleArea = cur_area;
						best = cur;
						best_intersection.value = intersection;
						//System.out.println("minTriangleArea=" + minTriangleArea);
					}
				}
				
				cur = cur.next;
			} while (cur != start);
			
			//If there is best than remove 2 points and put their intersection instead
			if (best == null) {
				break;
			}
			best_intersection.next = best.next.next.next;
			best.next = best_intersection;
			countOfPoints--;
			start = best;
		}
		
		//Compose result
		Point[] result = new Point[countOfPoints];
		while (countOfPoints > 0) {
			result[countOfPoints - 1] = start.value;
			start = start.next;
			countOfPoints--;
		}
		
		return result;
	}
	
	
	public static double triangleArea(Point A, Point B, Point C) {
		double area = (A.x * (B.y - C.y) + B.x * (C.y - A.y) + C.x * (A.y - B.y)) / 2.0;
		return Math.abs(area);
	}
	
	
    public static Point findIntersection(Point l1s, Point l1e, Point l2s, Point l2e) {
        
    	double a1 = l1e.y - l1s.y;
        double b1 = l1s.x - l1e.x;
        double c1 = a1 * l1s.x + b1 * l1s.y;
        
        double a2 = l2e.y - l2s.y;
        double b2 = l2s.x - l2e.x;
        double c2 = a2 * l2s.x + b2 * l2s.y;
        
        double delta = a1 * b2 - a2 * b1;
        if (delta == 0) {
        	return null;
        }
        
        return new Point((b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta);
    }
	
	
	public static MatOfPoint findBigestContour(List<MatOfPoint> contours) {
		MatOfPoint bigestContour = null;
		int bigestArea = 0;
		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint mop = contours.get(i);
			double contourArea = Imgproc.contourArea(mop);
			//Rect contourRec = Imgproc.boundingRect(mop);
			//int contourArea = contourRec.height * contourRec.width;
			if (contourArea > bigestArea) {
				bigestArea = (int) contourArea;
				bigestContour = mop;
			}
		}
		return bigestContour;
	}
	
	
	public static Point[] getOrderedCorners(Point[] cornersUnordered, double maxX, double maxY) {
		
		Point cornerTopLeft = new Point(0, 0);
		Point cornerTopRight = new Point(0, maxY);
		Point cornerBotRight = new Point(maxX, maxY);
		Point cornerBotLeft = new Point(maxX, 0);
		
		Point[] cornerPoints = new Point[4];
		
		cornerPoints[0] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerTopLeft, cornerPoints[0]) > distance(cornerTopLeft, cornersUnordered[i])) {
				cornerPoints[0] = cornersUnordered[i];
			}
		}

		cornerPoints[1] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerTopRight, cornerPoints[1]) > distance(cornerTopRight, cornersUnordered[i])) {
				cornerPoints[1] = cornersUnordered[i];
			}
		}
		
		cornerPoints[2] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerBotRight, cornerPoints[2]) > distance(cornerBotRight, cornersUnordered[i])) {
				cornerPoints[2] = cornersUnordered[i];
			}
		}
		
		cornerPoints[3] = cornersUnordered[0];
		for (int i = 0; i < cornersUnordered.length; i++) {
			if (distance(cornerBotLeft, cornerPoints[3]) > distance(cornerBotLeft, cornersUnordered[i])) {
				cornerPoints[3] = cornersUnordered[i];
			}
		}
		
		return cornerPoints;
	}
	
	
	public static double distance(Point p1, Point p2) {
		return Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
	}
	
	
	// To find orientation of ordered triplet (p, q, r). 
	// The function returns following values 
	// 0 --> p, q and r are colinear 
	// 1 --> Clockwise 
	// 2 --> Counterclockwise 
	public static int orientation(Point p, Point q, Point r) { 
	    double val = (q.y - p.y) * (r.x - q.x) - 
	              (q.x - p.x) * (r.y - q.y); 
	  
	    if (val == 0) return 0;  // colinear 
	    return (val > 0)? 1: 2; // clock or counterclock wise 
	} 
	
	
	// Prints convex hull of a set of n points. 
	public static Point[] convexHull(Point[] points) {
		
		int n = points.length;
		
	    // There must be at least 3 points 
	    if (n < 3) {
	    	throw new IllegalStateException("n < 3");
	    }
	  
	    // Initialize Result 
	    List<Point> hull_list = new ArrayList<Point>(); 
	  
	    // Find the leftmost point 
	    int l = 0; 
	    for (int i = 1; i < n; i++) 
	        if (points[i].x < points[l].x) 
	            l = i; 
	  
	    // Start from leftmost point, keep moving counterclockwise 
	    // until reach the start point again.  This loop runs O(h) 
	    // times where h is number of points in result or output. 
	    int p = l, q; 
	    do
	    { 
	        // Add current point to result 
	    	hull_list.add(points[p]); 
	  
	        // Search for a point 'q' such that orientation(p, x, 
	        // q) is counterclockwise for all points 'x'. The idea 
	        // is to keep track of last visited most counterclock- 
	        // wise point in q. If any point 'i' is more counterclock- 
	        // wise than q, then update q. 
	        q = (p+1)%n; 
	        for (int i = 0; i < n; i++) 
	        { 
	           // If i is more counterclockwise than current q, then 
	           // update q 
	           if (orientation(points[p], points[i], points[q]) == 2) 
	               q = i; 
	        } 
	  
	        // Now q is the most counterclockwise with respect to p 
	        // Set p as q for next iteration, so that q is added to 
	        // result 'hull' 
	        p = q; 
	  
	    } while (p != l);  // While we don't come to first point 
	  
	    Point[] hull = new Point[hull_list.size()];
	    hull_list.toArray(hull);
	    
	    return hull;
	}
	
	
	public static Point[] gen9HoughLinesCrossPoints(Mat source_gray) {
		
		ResultPair<List<HoughLine>, List<HoughLine>> all_lines = getHoughTransform(source_gray);
        //ResultPair<List<HoughLine>, List<HoughLine>> all_lines = getHoughTransform1(source_gray);
        List<HoughLine> h_lines = all_lines.getFirst();
        List<HoughLine> v_lines = all_lines.getSecond();
        System.out.println("gen9HoughLinesCrossPoints: h_lines.size=" + h_lines.size() + ", v_lines.size=" + v_lines.size());
        
        if (h_lines.size() < 9 || v_lines.size() < 9) {
        	return null;
        }
        
        //Limit lines to max 100, otherwise the algorithm is too slow
        if (h_lines.size() > 100) {
        	h_lines = genAvgLinesByKMeansClustering(100, h_lines);
        }
        
        if (v_lines.size() > 100) {
        	v_lines = genAvgLinesByKMeansClustering(100, v_lines);
        }
        
        int x_test1 = 0;
        int x_test2 = source_gray.width();
        List<Hough9Lines> hough9Lines_H = selectHough9Lines_Horizontal(source_gray.height() / 20, source_gray.height() / 5, x_test1, h_lines);
        System.out.println("gen9HoughLinesCrossPoints: hough9Lines_H.size=" + hough9Lines_H.size());
        if (hough9Lines_H.size() < 1) {
        	return null;
        }
        hough9Lines_H = correctErrorWithSecondPointX(hough9Lines_H, x_test2);
        
        
        int y_test1 = 0;
        int y_test2 = source_gray.height();
        List<Hough9Lines> hough9Lines_V = selectHough9Lines_Vertical(source_gray.height() / 20, source_gray.height() / 5, y_test1, v_lines);
        System.out.println("gen9HoughLinesCrossPoints: hough9Lines_V.size=" + hough9Lines_V.size());
        if (hough9Lines_V.size() < 1) {
        	return null;
        }
        hough9Lines_V = correctErrorWithSecondPointY(hough9Lines_V, y_test2);
        
        Hough9Lines horizontal9Lines = hough9Lines_H.get(0);
        Hough9Lines vertical9Lines = hough9Lines_V.get(0);
        
    	/*Mat toDraw = source_gray.clone();
    	drawHough9Lines(horizontal9Lines, toDraw);
    	drawHough9Lines(vertical9Lines, toDraw);
        HighGui.imshow("lines", toDraw);
        HighGui.waitKey(0);
        */
        
        List<Point> intersections = new ArrayList<Point>();
        for (int i = 0; i < horizontal9Lines.lines.size(); i++) {
        	for (int j = 0; j < vertical9Lines.lines.size(); j++) {
        		HoughLine line1 = horizontal9Lines.lines.get(i);
        		HoughLine line2 = vertical9Lines.lines.get(j);
        		Point intersection = findIntersection(line1.pt1, line1.pt2, line2.pt1, line2.pt2);
        		intersections.add(intersection);
        	}
        }
        
        return intersections.toArray(new Point[intersections.size()]);
	}
	
	
	private static ResultPair<List<HoughLine>, List<HoughLine>> getHoughTransform(Mat source_gray) {
		
		Mat blur = new Mat();
		Imgproc.GaussianBlur(source_gray, blur, new Size(15, 15), 0.5);
		//Imgproc.adaptiveThreshold(source_gray, source_gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
        //Imgproc.threshold(source_gray, source_gray, 10, 255, Imgproc.THRESH_BINARY);
		//Mat bilateralFilter = new Mat();
		//Imgproc.bilateralFilter(source_gray, bilateralFilter, 5, 150, 150);
		//source_gray = bilateralFilter;
		
        //HighGui.imshow("source_gray", source_gray);
        //HighGui.waitKey(0);
		
		Mat canny = new Mat();
		Imgproc.Canny(blur, canny, 20, 80);
		
        //HighGui.imshow("canny", canny);
        //HighGui.waitKey(0);
		
		Mat lines = new Mat();
		
		Imgproc.HoughLines(canny, lines, 1, Math.PI / 360, 120);
		
		double deltaAngleInDegrees = 10;
		
		List<HoughLine> h_lines = new ArrayList<HoughLine>();
		List<HoughLine> v_lines = new ArrayList<HoughLine>();
		for (int i = 0; i < lines.rows(); i++) {
		  	
		    double data[] = lines.get(i, 0);
		    double rho = data[0];
		    double theta = data[1];
		    
		    HoughLine line = new HoughLine(rho, theta);
		    
		    //System.out.println(theta);
		    
		    /*if (line.theta < Math.PI / 4 || line.theta > Math.PI - Math.PI / 4) {
		    	v_lines.add(line);
		    } else {
		    	h_lines.add(line);
		    }*/
		    if ((line.theta >= Math.toRadians(0 - deltaAngleInDegrees) && line.theta <= Math.toRadians(0 + deltaAngleInDegrees))
		    		|| (line.theta >= Math.toRadians(180 - deltaAngleInDegrees) && line.theta <= Math.toRadians(180 + deltaAngleInDegrees))) {
		    	v_lines.add(line);
		    } else if ((line.theta >= Math.toRadians(90 - deltaAngleInDegrees) && line.theta <= Math.toRadians(90 + deltaAngleInDegrees))
		    		|| (line.theta >= Math.toRadians(270 - deltaAngleInDegrees) && line.theta <= Math.toRadians(270 + deltaAngleInDegrees))) {
		    	h_lines.add(line);
		    }
		}
		
		return new ResultPair<List<HoughLine>, List<HoughLine>>(h_lines, v_lines);
	}
	
	
	private static ResultPair<List<HoughLine>, List<HoughLine>> getHoughTransform1(Mat source_gray) {
		
		Mat adapted = new Mat();
        Imgproc.adaptiveThreshold(source_gray, adapted, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 20);
        //HighGui.imshow("adapted", adapted);
        //HighGui.waitKey(0);
        
        Mat canny = new Mat();
		Imgproc.Canny(adapted, canny, 20, 80);
        //HighGui.imshow("canny", canny);
        //HighGui.waitKey(0);
		adapted = canny;
		
        
        double deltaAngleInDegrees = 10;
        
        Mat horizontal = adapted.clone();
        int horizontal_size = horizontal.cols() / 100;
        
        Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontal_size, 1));
        Imgproc.erode(horizontal, horizontal, horizontalStructure);
        Imgproc.dilate(horizontal, horizontal, horizontalStructure);
        
        //HighGui.imshow("horizontal", horizontal);
        //HighGui.waitKey(0);
        
		Mat horizontalLines = new Mat();
		Imgproc.HoughLines(horizontal, horizontalLines, 1, Math.PI / 720, 80);
		
		//System.out.println("horizontalLines size is " + horizontalLines.rows());
		
		//Mat toDraw = source_gray.clone();
		
		List<HoughLine> horizontalLinesList = new ArrayList<HoughLine>();
		for (int i = 0; i < horizontalLines.rows(); i++) {
		  	
		    double data[] = horizontalLines.get(i, 0);
		    double rho = data[0];
		    double theta = data[1];
		    
		    if ((theta >= Math.toRadians(90 - deltaAngleInDegrees) && theta <= Math.toRadians(90 + deltaAngleInDegrees))
		    		|| (theta >= Math.toRadians(270 - deltaAngleInDegrees) && theta <= Math.toRadians(270 + deltaAngleInDegrees))) {
		    	
			    HoughLine line = new HoughLine(rho, theta);
		    	horizontalLinesList.add(line);
		    	//Imgproc.line(toDraw, line.pt1, line.pt2, new Scalar(255, 255, 255), 2);
		    }
		}
		
        //HighGui.imshow("horizontalLines", toDraw);
        //HighGui.waitKey(0);
        
        List<HoughLine> avgHorizontalLinesList = genAvgLinesByKMeansClustering(54, horizontalLinesList);
		horizontalLinesList = avgHorizontalLinesList;
        
        Mat vertical = adapted.clone();
        int vertical_size = vertical.rows() / 100;
        Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, vertical_size));
        Imgproc.erode(vertical, vertical, verticalStructure);
        Imgproc.dilate(vertical, vertical, verticalStructure);
        
        //HighGui.imshow("vertical", vertical);
        //HighGui.waitKey(0);
        
		Mat verticalLines = new Mat();
		Imgproc.HoughLines(vertical, verticalLines, 1, Math.PI / 720, 80);
		
		//System.out.println("verticalLines size is " + verticalLines.rows());
		
		List<HoughLine> verticalLinesList = new ArrayList<HoughLine>();
		for (int i = 0; i < verticalLines.rows(); i++) {
		  	
		    double data[] = verticalLines.get(i, 0);
		    double rho = data[0];
		    double theta = data[1];
		    
		    if ((theta >= Math.toRadians(0 - deltaAngleInDegrees) && theta <= Math.toRadians(0 + deltaAngleInDegrees))
		    		|| (theta >= Math.toRadians(180 - deltaAngleInDegrees) && theta <= Math.toRadians(180 + deltaAngleInDegrees))) {
		    	
			    HoughLine line = new HoughLine(rho, theta);
			    verticalLinesList.add(line);
			    //Imgproc.line(toDraw, line.pt1, line.pt2, new Scalar(255, 255, 255), 2);
		    }
		}
        
        //HighGui.imshow("all", toDraw);
        //HighGui.waitKey(0);
		
        
		List<HoughLine> avgVerticalLinesList = genAvgLinesByKMeansClustering(54, verticalLinesList);
		verticalLinesList = avgVerticalLinesList;
		
        return new ResultPair<List<HoughLine>, List<HoughLine>>(horizontalLinesList, verticalLinesList);
	}


	private static List<HoughLine> genAvgLinesByKMeansClustering(int K, List<HoughLine> lines) {
		
		double[] linesRho = new double[lines.size()];
		for (int i = 0; i < lines.size(); i++) {
			linesRho[i] = lines.get(i).rho;
		}
		
		KMeansScalar rhoKMmeansClustering = new KMeansScalar(K, linesRho);
		
		List<HoughLine> avgLinesList = new ArrayList<HoughLine>();
		for (int centroidID = 0; centroidID < rhoKMmeansClustering.weights.length; centroidID++) {
			
			if (rhoKMmeansClustering.weights[centroidID] > 0) {
				
				double rho_sum = 0;
				double rho_cnt = 0;
				double theta_sum = 0;
				double theta_cnt = 0;
				
				for (int i = 0; i < lines.size(); i++) {
					HoughLine line = lines.get(i);
					if (rhoKMmeansClustering.centroids_ids[i] == centroidID) {
						rho_sum += line.rho;
						rho_cnt++;
						theta_sum += line.theta;
						theta_cnt++;
					}
				}
				
				HoughLine avgLine = new HoughLine(rho_sum / rho_cnt, theta_sum / theta_cnt);
				avgLinesList.add(avgLine);
			}
		}
		
		
		return avgLinesList;
	}
	
	
	private static List<Hough9Lines> selectHough9Lines_Horizontal(int start_interval, int end_interval, int x_test, List<HoughLine> lines) {
		
		lines = sortByYInX(lines, x_test);
		
		List<Hough9Lines> result = new ArrayList<Hough9Lines>();
        for (int i = 0; i < 0.25d * lines.size(); i++) {
        	for (double interval = start_interval; interval < end_interval; interval += 0.5) {
        		
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
        		
        		error_all = error_all / (double) (line9.calculateY(x_test) - y1);
        		
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
	
	
	private static List<Hough9Lines> selectHough9Lines_Vertical(int start_interval, int end_interval, int y_test, List<HoughLine> lines) {
		
		lines = sortByXInY(lines, y_test);
		
		List<Hough9Lines> result = new ArrayList<Hough9Lines>();
        for (int i = 0; i < 0.25d * lines.size(); i++) {
        	for (double interval = start_interval; interval < end_interval; interval += 0.5) {
        		
        		int line1Index = i;
        		HoughLine line1 = lines.get(line1Index);
        		double x1 = line1.calculateX(y_test);
        		double error1 = 0;
        		
        		int line2Index = findClosestLineIndexByY(line1Index + 1, lines, y_test, x1 + 1 * interval);
        		if (line2Index == -1) continue;
        		HoughLine line2 = lines.get(line2Index);
        		double error2 = Math.abs(line2.calculateX(y_test) - (x1 + 1 * interval));
        		
        		int line3Index = findClosestLineIndexByY(line2Index + 1, lines, y_test, x1 + 2 * interval);
        		if (line3Index == -1) continue;
        		HoughLine line3 = lines.get(line3Index);
        		double error3 = Math.abs(line3.calculateX(y_test) - (x1 + 2 * interval));
        		
        		int line4Index = findClosestLineIndexByY(line3Index + 1, lines, y_test, x1 + 3 * interval);
        		if (line4Index == -1) continue;
        		HoughLine line4 = lines.get(line4Index);
        		double error4 = Math.abs(line4.calculateX(y_test) - (x1 + 3 * interval));
        		
        		int line5Index = findClosestLineIndexByY(line4Index + 1, lines, y_test, x1 + 4 * interval);
        		if (line5Index == -1) continue;
        		HoughLine line5 = lines.get(line5Index);
        		double error5 = Math.abs(line5.calculateX(y_test) - (x1 + 4 * interval));
        		
        		int line6Index = findClosestLineIndexByY(line5Index + 1, lines, y_test, x1 + 5 * interval);
        		if (line6Index == -1) continue;
        		HoughLine line6 = lines.get(line6Index);
        		double error6 = Math.abs(line6.calculateX(y_test) - (x1 + 5 * interval));
        		
        		int line7Index = findClosestLineIndexByY(line6Index + 1, lines, y_test, x1 + 6 * interval);
        		if (line7Index == -1) continue;
        		HoughLine line7 = lines.get(line7Index);
        		double error7 = Math.abs(line7.calculateX(y_test) - (x1 + 6 * interval));
        		
        		int line8Index = findClosestLineIndexByY(line7Index + 1, lines, y_test, x1 + 7 * interval);
        		if (line8Index == -1) continue;
        		HoughLine line8 = lines.get(line8Index);
        		double error8 = Math.abs(line8.calculateX(y_test) - (x1 + 7 * interval));
        		
        		int line9Index = findClosestLineIndexByY(line8Index + 1, lines, y_test, x1 + 8 * interval);
        		if (line9Index == -1) continue;
        		HoughLine line9 = lines.get(line9Index);
        		double error9 = Math.abs(line9.calculateX(y_test) - (x1 + 8 * interval));
        		
        		double error_all = error1 + error2 + error3 + error4 + error5 + error6 + error7 + error8 + error9;
        		
        		error_all = error_all / (double) (line9.calculateX(y_test) - x1);
        		
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
	
	
	private static List<Hough9Lines> correctErrorWithSecondPointY(List<Hough9Lines> hough9Lines, int y_test) {
        
		for (int i = 0; i < hough9Lines.size(); i++) {
        	
        	Hough9Lines lines9 = hough9Lines.get(i);
        	double minX = Double.MAX_VALUE;
        	double maxX = Double.MIN_VALUE;
        	for (int j = 0; j < lines9.lines.size(); j++) {
        		HoughLine line = lines9.lines.get(j);
        		double x2 = line.calculateX(y_test);
        		if (x2 < minX) {
        			minX = x2;
        		}
        		if (x2 > maxX) {
        			maxX = x2;
        		}
        	}
        	
        	double errorCorrection = 0;
        	for (int j = 0; j < lines9.lines.size(); j++) {
        		HoughLine line = lines9.lines.get(j);
        		double expectedX = minX + ((maxX - minX) * j) / (double) (lines9.lines.size() - 1);
        		errorCorrection += Math.abs(expectedX - line.calculateX(y_test));
        	}
        	
        	lines9.error += errorCorrection;
        }
		
		return sortByError(hough9Lines);
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


	private static int findClosestLineIndexByY(int startIndex, List<HoughLine> lines, double y_test, double x_test) {
		
		int bestMatchIndex = -1;
		double bestMatchDelta = Double.MAX_VALUE;
		
		for (int i = startIndex; i < lines.size(); i++) {
			HoughLine line = lines.get(i);
			double delta = Math.abs(line.calculateX(y_test) - x_test);
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
	
	
	private static List<HoughLine> sortByXInY(List<HoughLine> lines, final int y) {
		
		HoughLine[] array = lines.toArray(new HoughLine[lines.size()]);
		
		Arrays.sort(array, new Comparator<HoughLine>() {
			@Override
			public int compare(HoughLine l1, HoughLine l2) {
				double x1 = l1.calculateX(y);
				double x2 = l2.calculateX(y);
				if (x1 > x2) {
					return 1;
				} else if (x1 < x2) {
					return -1;
				} else {
					return -1;
				}
			}
		});
		
		return Arrays.asList(array);
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
	
	
	private static void drawHough9Lines(Hough9Lines lines, Mat toDraw) {
		Imgproc.line(toDraw, lines.lines.get(0).pt1, lines.lines.get(0).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(1).pt1, lines.lines.get(1).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(2).pt1, lines.lines.get(2).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(3).pt1, lines.lines.get(3).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(4).pt1, lines.lines.get(4).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(5).pt1, lines.lines.get(5).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(6).pt1, lines.lines.get(6).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(7).pt1, lines.lines.get(7).pt2, new Scalar(255, 255, 255), 2);
    	Imgproc.line(toDraw, lines.lines.get(8).pt1, lines.lines.get(8).pt2, new Scalar(255, 255, 255), 2);
	}
	
	
	private static final class ListItem<T> {
		public T value;
		public ListItem<T> next;
	}
	
	
	public static final class HoughLine {
		
		
		// x *  Cos(Theta) + y * sin(Theta) - Rho = 0
		public double rho;
		public double theta;
		
		//double y = pt1.y + ((pt2.y - pt1.y) * (x - pt1.x)) / (double) (pt2.x - pt1.x);
		public Point pt1;
		public Point pt2;
		
		
		public HoughLine(double _rho, double _theta) {
			
			rho = _rho;
			theta = _theta;
			
	        double cosTheta = Math.cos(theta);
	        double sinTheta = Math.sin(theta);
	        double x0 = cosTheta * rho;
	        double y0 = sinTheta * rho;
	        
	        pt1 = new Point(x0 + 100000 * (-sinTheta), y0 + 100000 * cosTheta);
	        pt2 = new Point(x0 - 100000 * (-sinTheta), y0 - 100000 * cosTheta);
		}
		
		
		public double calculateY(double x) {
			
	        double cosTheta = Math.cos(theta);
	        double sinTheta = Math.sin(theta);
			
	        double y = (rho - x * cosTheta) / sinTheta;
	        
			return y;
		}
		
		
		public double calculateX(double y) {
			
	        double cosTheta = Math.cos(theta);
	        double sinTheta = Math.sin(theta);
			
	        double x = (rho - y * sinTheta) / cosTheta;
	        
			return x;
		}
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
