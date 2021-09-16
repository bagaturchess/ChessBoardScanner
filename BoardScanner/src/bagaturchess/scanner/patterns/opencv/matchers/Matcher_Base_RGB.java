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
package bagaturchess.scanner.patterns.opencv.matchers;


import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.Color;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.KMeansScalar;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.Matcher_Base;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class Matcher_Base_RGB extends Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT_START = 0.65f;
	private static final float SIZE_DELTA_PERCENT_END = 0.85f;


	public Matcher_Base_RGB(BoardProperties _imageProperties, String _displayName) {
		super(_imageProperties, _displayName);
	}
	
	
	public ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) throws IOException {
		
		int[][][] rgbBoard = (int[][][]) boardMatrix;
		
		if (rgbBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + rgbBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		
		MatchingData matchingData = buildMatchingData(rgbBoard, matchingInfo);
		
		
		double[] deltas = new double[64];
		for (int squareID = 0; squareID < 64; squareID++) {
			deltas[squareID] = matchingData.squareData[squareID].delta;
		}
		
		KMeansScalar deltas_clusters = new KMeansScalar(5, deltas);
		
		//Set<Integer> emptySquares = new HashSet<Integer>();
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares_Heuristic1(rgbBoard, 0.95d);
		emptySquares.addAll(MatrixUtils.getEmptySquares_Heuristic2(rgbBoard));
		
		int[] pids = new int[64];
		
		for (int squareID = 0; squareID < 64; squareID++) {
			
			if (deltas_clusters.centroids_ids[squareID] <= 2 && !emptySquares.contains(squareID)) {
				
				pids[squareID] = matchingData.pieceIDs[squareID];
				System.out.println("Square " + squareID + " is in centroid " + deltas_clusters.centroids_ids[squareID] + " and has PID " + pids[squareID]);
				
			} else {
				
				pids[squareID] = Constants.PID_NONE;
			}
		}
		
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		for (int squareID = 0; squareID < 64; squareID++) {
			MatrixUtils.PatternMatchingData squareData = matchingData.squareData[squareID];
			result.totalDelta += squareData.delta;
		}
		result.totalDelta = result.totalDelta / (double) 64;
		
		
		return new ResultPair<String, MatchingStatistics>(BoardUtils.createFENFromPIDs(pids), result);
	}
	
	
	private MatchingData buildMatchingData(int[][][] rgbBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (rgbBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + rgbBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingData result = new MatchingData();
		
		if (matchingInfo != null) matchingInfo.setPhaseName(getDisplayName());
		
		ResultPair<Color, Color> bgcolorsOfSquares = MatrixUtils.getSquaresColor_RGB(rgbBoard);
		
		int countPIDs = 0;
		for (int i = 0; i < rgbBoard.length; i += rgbBoard.length / 8) {
			for (int j = 0; j < rgbBoard.length; j += rgbBoard.length / 8) {
				
				int file = i / (rgbBoard.length / 8);
				int rank = j / (rgbBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (matchingInfo != null) matchingInfo.setSquare(fieldID);
				
				int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(rgbBoard, i, j);
				//int bgcolor_avg = (int) MatrixUtils.calculateColorStats(squareMatrix).getEntropy();
				
				/*MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
				bestPatternData.x = 0;
				bestPatternData.y = 0;
				bestPatternData.size = squareMatrix.length;
				ImageHandlerSingleton.getInstance().printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
				*/
				
				List<Color> bgcolors = new ArrayList<Color>();
				//bgcolors.add(bgcolor_avg);
				bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
				
				ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData = getPID(squareMatrix, bgcolors, getAllPIDs(fieldID), fieldID);
				result.pieceIDs[fieldID] = pidAndData.getFirst();
				result.squareData[fieldID] = pidAndData.getSecond();
				
				//System.out.println("Square " + fieldID + " has delta " + pidAndData.getSecond().delta);
				
				countPIDs++;
				if (matchingInfo != null) matchingInfo.setCurrentPhaseProgress(countPIDs / (double) 64);
			}
		}
		
		return result;
	}


	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][][] rgbSquareMatrix,
			List<Color> bgcolors, Set<Integer> pids, int fieldID) throws IOException {
		
		Object rgbSquare = ImageHandlerSingleton.getInstance().createRGBImage(rgbSquareMatrix);
		Mat rgbSource = ImageHandlerSingleton.getInstance().graphic2Mat(rgbSquare);
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		int maxSize = rgbSquareMatrix.length;
		int startSize = (int) (SIZE_DELTA_PERCENT_START * maxSize);
		int endSize = (int) (SIZE_DELTA_PERCENT_END * maxSize);
		
		int counter = 0;
		
		for (int size = startSize; size <= endSize; size++) {
			
			MatrixUtils.PatternMatchingData curData_best  = null;
			
			for (Integer pid : pids) {
				
				if (pid == Constants.PID_NONE && size != endSize) {
					continue;
				}
				
				for (int i = 0; i < bgcolors.size(); i++) {
					
			        MatrixUtils.PatternMatchingData curData = new MatrixUtils.PatternMatchingData();
			        
					Color bgcolor = bgcolors.get(i);
					
					Object rgbPattern = null;
					Mat rgbTemplate = null;
					if (pid == Constants.PID_NONE) {
						
						int[][][] emptySquare_matrix = createSquareImage(bgcolor, size);
						
						rgbPattern = ImageHandlerSingleton.getInstance().createRGBImage(emptySquare_matrix);
						
						//ImageHandlerSingleton.getInstance().saveImage("X" + System.nanoTime(), "png", rgbPattern);
						curData.pattern_rgb = emptySquare_matrix;
						
						rgbTemplate = ImageHandlerSingleton.getInstance().graphic2Mat(rgbPattern);	
					} else {
						
						rgbPattern = ImageHandlerSingleton.getInstance().createPieceImage_RGB(boardProperties.getPiecesSetFileNamePrefix(), pid, bgcolor, size);
						
						//int[][] grayMatrix = ImageHandlerSingleton.getInstance().convertToGrayMatrix(grayPattern);
						//curData.pattern = grayMatrix;
						rgbTemplate = ImageHandlerSingleton.getInstance().graphic2Mat(rgbPattern);	
					}
					
			        Mat outputImage = new Mat();
			        Imgproc.matchTemplate(rgbSource, rgbTemplate, outputImage, Imgproc.TM_CCOEFF_NORMED);
			        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
			        
			        rgbTemplate.release();
			        
			        ImageHandlerSingleton.getInstance().releaseGraphic(rgbPattern);
			        
			        curData.size = size;
			        curData.delta = 1 - mmr.maxVal;
			        //System.out.println(curData.delta);
			        
					if (curData_best == null || curData_best.delta > curData.delta) {
						curData_best = curData;
					}
				}
				
				if (bestData == null || bestData.delta > curData_best.delta) {
					bestData = curData_best;
					bestPID = pid;
					
					//ImageHandlerSingleton.getInstance().printInfo(bestData, "" + fieldID + "_best" + (counter++));
				}
			}
		}
		
		rgbSource.release();
		
		ImageHandlerSingleton.getInstance().releaseGraphic(rgbSource);
		
		//ImageHandlerSingleton.getInstance().printInfo(graySquareMatrix, bestData, "" + fieldID + "_matching");
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	private int[][][] createSquareImage(Color bgcolor, int size) {
		
		int contourColor = 0;
		
		int[][][] result = new int[size][size][3];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				
				result[i][j][0] = bgcolor.red;
				result[i][j][1] = bgcolor.green;
				result[i][j][2] = bgcolor.blue;
				
				if (i == j || i == size - j - 1) {
					result[i][j][0] = contourColor;
					result[i][j][1] = contourColor;
					result[i][j][2] = contourColor;
				}
			}
		}
		
		/*for (int i = 0; i < size; i++){
			result[i][0] = contourColor;
			result[i][size - 1] = contourColor;
		}
		
		for (int i = 0; i < size; i++){
			result[0][i] = contourColor;
			result[size - 1][i] = contourColor;
		}*/
		
		return result;
	}


	private Set<Integer> getAllPIDs(int fieldID) {
		Set<Integer> pidsToSearch = new HashSet<Integer>();
		pidsToSearch.add(Constants.PID_NONE);
		if (fieldID >= 8 && fieldID <= 55) pidsToSearch.add(Constants.PID_W_PAWN);
		pidsToSearch.add(Constants.PID_W_KNIGHT);
		pidsToSearch.add(Constants.PID_W_BISHOP);
		pidsToSearch.add(Constants.PID_W_ROOK);
		pidsToSearch.add(Constants.PID_W_QUEEN);
		pidsToSearch.add(Constants.PID_W_KING);
		if (fieldID >= 8 && fieldID <= 55) pidsToSearch.add(Constants.PID_B_PAWN);
		pidsToSearch.add(Constants.PID_B_KNIGHT);
		pidsToSearch.add(Constants.PID_B_BISHOP);
		pidsToSearch.add(Constants.PID_B_ROOK);
		pidsToSearch.add(Constants.PID_B_QUEEN);
		pidsToSearch.add(Constants.PID_B_KING);
		return pidsToSearch;
	}
	
	
	private Set<Integer> getPiecesPIDs(int fieldID) {
		Set<Integer> pidsToSearch = new HashSet<Integer>();
		if (fieldID >= 8 && fieldID <= 55) pidsToSearch.add(Constants.PID_W_PAWN);
		pidsToSearch.add(Constants.PID_W_KNIGHT);
		pidsToSearch.add(Constants.PID_W_BISHOP);
		pidsToSearch.add(Constants.PID_W_ROOK);
		pidsToSearch.add(Constants.PID_W_QUEEN);
		pidsToSearch.add(Constants.PID_W_KING);
		if (fieldID >= 8 && fieldID <= 55) pidsToSearch.add(Constants.PID_B_PAWN);
		pidsToSearch.add(Constants.PID_B_KNIGHT);
		pidsToSearch.add(Constants.PID_B_BISHOP);
		pidsToSearch.add(Constants.PID_B_ROOK);
		pidsToSearch.add(Constants.PID_B_QUEEN);
		pidsToSearch.add(Constants.PID_B_KING);
		return pidsToSearch;
	}
	
	
	private Set<Integer> getEmptyPID(int fieldID) {
		Set<Integer> pidsToSearch = new HashSet<Integer>();
		pidsToSearch.add(Constants.PID_NONE);
		return pidsToSearch;
	}
	
	
	private static class MatchingData {
		private int[] pieceIDs = new int[64];
		private MatrixUtils.PatternMatchingData[] squareData = new MatrixUtils.PatternMatchingData[64];
	}
}
