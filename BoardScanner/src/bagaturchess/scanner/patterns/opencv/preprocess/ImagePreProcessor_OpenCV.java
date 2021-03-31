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
package bagaturchess.scanner.patterns.opencv.preprocess;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Base;
import bagaturchess.scanner.patterns.opencv.OpenCVUtils;


public class ImagePreProcessor_OpenCV extends ImagePreProcessor_Base {
	
	
	private MatOfPoint2f targetCorners;
	
	
	public ImagePreProcessor_OpenCV(BoardProperties _boardProperties) throws IOException {
		
		super(_boardProperties);
		
		Object whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(254);
		Object blackSquareColor = ImageHandlerSingleton.getInstance().getColor(0);
		Object targetPerspective_obj = ImageHandlerSingleton.getInstance().createBoardImage(boardProperties,
				"8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);
		
		//ImageHandlerSingleton.getInstance().saveImage("OpenCV_target", "png", targetPerspective_obj);
		
		Mat targetPerspective = ImageHandlerSingleton.getInstance().graphic2Mat(targetPerspective_obj);
		MatOfPoint2f corners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(targetPerspective, new Size(7, 7), corners);
		if (!found) {
			throw new IllegalStateException("Chess board not found in the generated image.");
		}
		Point[] targetCorners_array = OpenCVUtils.getOrderedCorners(corners.toArray(), targetPerspective.width(), targetPerspective.height());
		
		targetCorners = new MatOfPoint2f();
		targetCorners.fromArray(targetCorners_array);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_input", "png", image);
		
		Mat source_rgb = ImageHandlerSingleton.getInstance().graphic2Mat(image);
		
		//Mat source_gray = new Mat(source_rgb.height(), source_rgb.width(), CvType.CV_8UC4);
		//Imgproc.cvtColor(source_rgb, source_gray, Imgproc.COLOR_BGR2GRAY);
		
		//Experiments.tryit(source_rgb);
		
		Mat result = findChessBoardCornersByBuildInFunction(source_rgb);
		
		if (result == null) {
			
			result = findChessBoardCornersByHoughLines(source_rgb);
			
			if (result == null) {
				
				result = findChessBoardCornersByContour(source_rgb);
			}
		}
		
		if (result == null) {
			return image;
		}
        
		Object resultObj = ImageHandlerSingleton.getInstance().mat2Graphic(result);
		
		ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_result", "png", resultObj);
		
		return resultObj;
	}
	
	
	public Mat findChessBoardCornersByBuildInFunction(Mat source_rgb) {
		
		MatOfPoint2f corners = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(source_rgb, new Size(7, 7), corners);
		
		if (found && !corners.empty()) {
			
	    	/*Mat toDraw = source_rgb.clone();
	    	MatOfPoint points = new MatOfPoint();
	    	corners.convertTo(points, CvType.CV_32S);
	    	List<MatOfPoint> contourTemp = new ArrayList<>();
	    	contourTemp.add(points);
	    	Imgproc.drawContours(toDraw, contourTemp, -1, new Scalar(255, 255, 255));
	        HighGui.imshow("lines", toDraw);
	        HighGui.waitKey(0);
	        */
			
			Mat result = new Mat();
			
			MatOfPoint2f corners_ordered = new MatOfPoint2f();
			corners_ordered.fromArray(OpenCVUtils.getOrderedCorners(corners.toArray(), source_rgb.width(), source_rgb.height()));
			
			Mat H = Calib3d.findHomography(corners_ordered, targetCorners);
			
			Imgproc.warpPerspective(source_rgb, result, H, source_rgb.size());
			
			System.out.println("ImagePreProcessor_OpenCV: Chess board found in a standard way.");
			
			return result;
		}
		
		return null;
	}
	
	
	private Mat findChessBoardCornersByHoughLines(Mat source_rgb) {
		
		Mat source_gray = new Mat(source_rgb.height(), source_rgb.width(), CvType.CV_8UC4);
		Imgproc.cvtColor(source_rgb, source_gray, Imgproc.COLOR_BGR2GRAY);
        
		Point[] intersections = OpenCVUtils.gen9HoughLinesCrossPoints(source_gray);
		if (intersections == null) {
			return null;
		}
		
		Point[] boardCorners = OpenCVUtils.getOrderedCorners(intersections, source_gray.width(), source_gray.height());
		
		MatOfPoint2f src = new MatOfPoint2f(
				boardCorners[0],
				boardCorners[1],
				boardCorners[2],
				boardCorners[3]);
		
		MatOfPoint2f dst = new MatOfPoint2f(
		        new Point(0, 0),
		        new Point(0, source_rgb.height()),
		        new Point(source_rgb.width(), source_rgb.height()),
		        new Point(source_rgb.width(), 0)      
		        );
		
		Mat result = new Mat();
		Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
		Imgproc.warpPerspective(source_rgb, result, warpMat, source_rgb.size());
		
		System.out.println("ImagePreProcessor_OpenCV: Chess board found by HoughLines.");
		
        //HighGui.imshow("result", result);
        //HighGui.waitKey(0);
		
		return result;
	}
	
	
	private Mat findChessBoardCornersByContour(Mat source_rgb) {
		
		//HighGui.imshow("source_rgb", source_rgb);
		//HighGui.waitKey(0);
		
		Mat blur = new Mat();
		Imgproc.GaussianBlur(source_rgb, blur, new Size(55, 55), 1.6);
		
		//HighGui.imshow("blur", blur);
		//HighGui.waitKey(0);
		
		/*int kernelSize = 2;
        Mat element = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_CROSS, new Size(2 * kernelSize + 1, 2 * kernelSize + 1),
                new Point(kernelSize, kernelSize));
        Imgproc.erode(source_gray, source_gray, element);*/
        //Imgproc.dilate(source_gray, source_gray, element);
		
        
		Mat canny = new Mat();
		Imgproc.Canny(blur, canny, 20, 80);
		
		//HighGui.imshow("canny", canny);
		//HighGui.waitKey(0);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
    	/*Mat toDraw = source_rgb.clone();
    	Imgproc.drawContours(toDraw, contours, -1, new Scalar(255, 255, 255));
        HighGui.imshow("contours", toDraw);
        HighGui.waitKey(0);
        */
		
		MatOfPoint bigestContour = OpenCVUtils.findBigestContour(contours);
		
		if (bigestContour == null) {
			return null;
		}
		
    	/*Mat toDraw = source_rgb.clone();
    	List<MatOfPoint> contourTemp = new ArrayList<>();
    	contourTemp.add(bigestContour);
    	Imgproc.drawContours(toDraw, contourTemp, -1, new Scalar(255, 255, 255));
        HighGui.imshow("bigestContour", toDraw);
        HighGui.waitKey(0);
		*/
		
		
		Point[] hullContour = OpenCVUtils.convexHull(bigestContour.toArray());
		
		/*Mat toDraw = source_rgb.clone();
		for (int i = 0; i < hullContour.length; i++ ) {
			Imgproc.drawMarker(toDraw, hullContour[i], new Scalar(255, 255, 255));
		}
		HighGui.imshow("hull", toDraw);
		HighGui.waitKey(0);
		*/
		
		MatOfPoint2f curve = new MatOfPoint2f(hullContour);
		double epsilon = 0.005 * Imgproc.arcLength(curve, true);
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		Imgproc.approxPolyDP(curve, approxCurve, epsilon, true);
		
		Point[] approxCurve_points = approxCurve.toArray();
		
		System.out.println("ImagePreProcessor_OpenCV: Chess board found by contours with " + approxCurve_points.length + " points.");
		
		/*Mat toDraw = source_rgb.clone();
		List<MatOfPoint> curve_in_list = new ArrayList<MatOfPoint>();
		curve_in_list.add(new MatOfPoint(approxCurve.toArray()));
		Imgproc.drawContours(toDraw, curve_in_list, 0, new Scalar(255, 255, 255));
		HighGui.imshow("curve_in_list", toDraw);
		HighGui.waitKey(0);
		*/
		
		
		if (approxCurve_points.length > 4 ) {
			
	        Rect boundingRec = Imgproc.boundingRect(bigestContour);
	        OpenCVUtils.extendRect(boundingRec, 0.05);
	        
	        /*Mat bounding = new Mat(source_rgb, boundingRec);
			HighGui.imshow("bounding", bounding);
			HighGui.waitKey(0);
			*/
	        
			approxCurve_points = OpenCVUtils.getMinimalQuadrilateral(approxCurve_points, boundingRec);
			
			/*Mat toDraw = source_rgb.clone();
			for (int i = 0; i < approxCurve_points.length; i++ ) {
				Imgproc.drawMarker(toDraw, approxCurve_points[i], new Scalar(255, 255, 255));
			}
			HighGui.imshow("getMinimalQuadrilateral", toDraw);
			HighGui.waitKey(0);
			*/
		}
		
		Point[] corners_of_contour = OpenCVUtils.getOrderedCorners(approxCurve_points, source_rgb.width(), source_rgb.height());
		
		MatOfPoint2f src = new MatOfPoint2f(
				corners_of_contour[0],
				corners_of_contour[1],
				corners_of_contour[2],
				corners_of_contour[3]);
		
		MatOfPoint2f dst = new MatOfPoint2f(
		        new Point(0, 0),
		        new Point(0, source_rgb.height()),
		        new Point(source_rgb.width(), source_rgb.height()),
		        new Point(source_rgb.width(), 0)      
		        );
		
		Mat result = new Mat();
		Mat warpMat = Imgproc.getPerspectiveTransform(src, dst);
		Imgproc.warpPerspective(source_rgb, result, warpMat, source_rgb.size());
		
        /*HighGui.imshow("Draw matches", result);
        HighGui.waitKey(0);
        
        Rect boundingRec = Imgproc.boundingRect(bigestContour);
        result = new Mat(source, boundingRec);*/
        
        //HighGui.imshow("Draw matches", result);
        //HighGui.waitKey(0);
		
		return result;
	}
}
