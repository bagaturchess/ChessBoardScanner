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


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgproc.Imgproc;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.KMeansLines_Scalar;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.common.ResultTriplet;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public abstract class Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT_START = 0.65f;
	private static final float SIZE_DELTA_PERCENT_END = 0.85f;
	
	
	protected BoardProperties boardProperties;
	
	
	protected Matcher_Base(BoardProperties _imageProperties) {
		boardProperties = _imageProperties;
	}
	
	
	public String getPiecesSetName() {
		return boardProperties.getPiecesSetFileNamePrefix();
	}
	
	
	public ResultTriplet<String, MatchingStatistics, Double> scan(int[][] grayBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + grayBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingData matchingData = buildMatchingData(grayBoard, matchingInfo);
		
		double emptySquareThreshold = 1;
		double maxFullThreshold = 0;
		
		/*boolean whileCondition = true;
		while (whileCondition && emptySquareThreshold > 0.7d) {
			
			emptySquareThreshold -= 0.01d;
			Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard, emptySquareThreshold);
			//System.out.println("Empty squares: " + emptySquares);
			
			double maxFull = 0f;
			double minEmpty = 1f;
			
			for (int fieldID = 0; fieldID < 64; fieldID++) {
				
				int pieceID = matchingData.pieceIDs[fieldID];
				MatrixUtils.PatternMatchingData squareData = matchingData.squareData[fieldID];
				
				if (pieceID == Constants.PID_NONE || emptySquares.contains(fieldID)) {
					//System.out.println(fieldID + " EMPTY " + squareData.delta);
					if (minEmpty > squareData.delta) {
						minEmpty = squareData.delta;
					}
				} else {
					//System.out.println(fieldID + " FULL " + squareData.delta);
					if (maxFull < squareData.delta) {
						maxFull = squareData.delta;
					}
				}
			}
			
			System.out.println("emptySquareThreshold=" + emptySquareThreshold + ", maxFull=" + maxFull + ", minEmpty=" + minEmpty);
			
			if (maxFull < minEmpty) {
				whileCondition = false;
				maxFullThreshold = maxFull;
			}
		}*/
		
		
		int[] pids = new int[64];
		
		if (maxFullThreshold > 0) {//Already resolved in the loop above
			
			for (int fieldID = 0; fieldID < 64; fieldID++) {
				
				int pieceID = matchingData.pieceIDs[fieldID];
				MatrixUtils.PatternMatchingData squareData = matchingData.squareData[fieldID];
				
				pids[fieldID] = squareData.delta <= maxFullThreshold ? pieceID : Constants.PID_NONE;
			}
			
		} else {//No solution found yet, so try with Kmeans clustering of deltas
			
			double[] deltas = new double[64];
			for (int fieldID = 0; fieldID < 64; fieldID++) {
				MatrixUtils.PatternMatchingData squareData = matchingData.squareData[fieldID];
				deltas[fieldID] = squareData.delta;
			}
			
			KMeansLines_Scalar kmeans = new KMeansLines_Scalar(9, deltas);
			
			Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard, 0.9d);
			
			for (int fieldID = 0; fieldID < 64; fieldID++) {
				
				if (kmeans.centroids_ids[fieldID] <= 5 && !emptySquares.contains(fieldID)) {
					
					pids[fieldID] = matchingData.pieceIDs[fieldID];
					System.out.println("Square " + fieldID + " is in centroid " + kmeans.centroids_ids[fieldID] + " and has PID " + pids[fieldID]);
					
				} else {
					
					pids[fieldID] = Constants.PID_NONE;
				}
			}
		}
		
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		for (int fieldID = 0; fieldID < 64; fieldID++) {
			MatrixUtils.PatternMatchingData squareData = matchingData.squareData[fieldID];
			result.totalDelta += squareData.delta;
		}
		result.totalDelta = result.totalDelta / (double) 64;
		
		
		return new ResultTriplet<String, MatchingStatistics, Double> (BoardUtils.createFENFromPIDs(pids), result, maxFullThreshold);
	}
	
	
	private MatchingData buildMatchingData(int[][] grayBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + grayBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingData result = new MatchingData();
		
		if (matchingInfo != null) matchingInfo.setPhaseName(this.getClass().getSimpleName());
		
		ResultPair<Integer, Integer> bgcolorsOfSquares = MatrixUtils.getSquaresColor(grayBoard);
		
		int countPIDs = 0;
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (matchingInfo != null) matchingInfo.setSquare(fieldID);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				//int bgcolor_avg = (int) MatrixUtils.calculateColorStats(squareMatrix).getEntropy();
				
				MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
				bestPatternData.x = 0;
				bestPatternData.y = 0;
				bestPatternData.size = squareMatrix.length;
				ImageHandlerSingleton.getInstance().printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
				
				List<Integer> bgcolors = new ArrayList<Integer>();
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
	
	
	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][] graySquareMatrix,
			List<Integer> bgcolors, Set<Integer> pids, int fieldID) throws IOException {
		
		Object graySquare = ImageHandlerSingleton.getInstance().createGrayImage(graySquareMatrix);
		Mat graySource = ImageHandlerSingleton.getInstance().graphic2Mat(graySquare);
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		int maxSize = graySquareMatrix.length;
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
			        
					int bgcolor = bgcolors.get(i);
					
					Object grayPattern = null;
					Mat garyTemplate = null;
					if (pid == Constants.PID_NONE) {
						int[][] emptySquare_matrix = createSquareImage(bgcolor, size);
						grayPattern = ImageHandlerSingleton.getInstance().createGrayImage(emptySquare_matrix);
						//ImageHandlerSingleton.getInstance().saveImage("X", "png", grayPattern);
						curData.pattern = emptySquare_matrix;
						garyTemplate = ImageHandlerSingleton.getInstance().graphic2Mat(grayPattern);	
					} else {
						grayPattern = ImageHandlerSingleton.getInstance().createPieceImage(boardProperties.getPiecesSetFileNamePrefix(), pid, bgcolor, size);
						//int[][] grayMatrix = ImageHandlerSingleton.getInstance().convertToGrayMatrix(grayPattern);
						//curData.pattern = grayMatrix;
						garyTemplate = ImageHandlerSingleton.getInstance().graphic2Mat(grayPattern);	
					}
					
			        Mat outputImage = new Mat();
			        Imgproc.matchTemplate(graySource, garyTemplate, outputImage, Imgproc.TM_CCOEFF_NORMED);
			        MinMaxLocResult mmr = Core.minMaxLoc(outputImage);
			        
			        garyTemplate.release();
			        
			        ImageHandlerSingleton.getInstance().releaseGraphic(grayPattern);
			        
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
		
		graySource.release();
		
		ImageHandlerSingleton.getInstance().releaseGraphic(graySquare);
		
		//ImageHandlerSingleton.getInstance().printInfo(graySquareMatrix, bestData, "" + fieldID + "_matching");
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	private int[][] createSquareImage(int bgcolor, int size) {
		
		int contourColor = 0;
		
		int[][] result = new int[size][size];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				result[i][j] = bgcolor;
				if (i == j) {
					result[i][j] = contourColor;
				}
				if (i == size - j - 1) {
					result[i][j] = contourColor;
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
