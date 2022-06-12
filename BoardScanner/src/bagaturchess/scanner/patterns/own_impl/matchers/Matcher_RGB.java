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
package bagaturchess.scanner.patterns.own_impl.matchers;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.Color;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.Matcher_Base;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class Matcher_RGB extends Matcher_Base {
	
	
	private static final float SIZE_DELTA_PERCENT_START = 0.75f;
	private static final float SIZE_DELTA_PERCENT_END = 0.99f;
	
	
	public Matcher_RGB(BoardProperties _imageProperties, String displayName) {
		super(_imageProperties, displayName);
	}
	
	
	protected double getTotalDeltaThreshold() {
		return 320;
	}
	
	
	public ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) {
		
		int[][][] rgbBoard = (int[][][]) boardMatrix;
		
		if (rgbBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + rgbBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingStatistics result = new MatchingStatistics();
		result.matcherName = this.getClass().getCanonicalName();
		
		if (matchingInfo != null) matchingInfo.setPhaseName(this.getClass().getSimpleName());
		
		
		//Set<Integer> emptySquares = new HashSet<Integer>();
		//Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard, 0.9d);
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares_Heuristic1(rgbBoard, 0.95d);
		emptySquares.addAll(MatrixUtils.getEmptySquares_Heuristic2(rgbBoard));
		//System.out.println(emptySquares);
		
		ResultPair<Color, Color> bgcolorsOfSquares = MatrixUtils.getSquaresColor_RGB(rgbBoard);
		
		int[] pids = new int[64];
		int countAll = 0;
		int countPIDs = 0;
		for (int i = 0; i < rgbBoard.length; i += rgbBoard.length / 8) {
			for (int j = 0; j < rgbBoard.length; j += rgbBoard.length / 8) {
				
				int file = i / (rgbBoard.length / 8);
				int rank = j / (rgbBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				pids[fieldID] = Constants.PID_NONE;
				
				if (matchingInfo != null) matchingInfo.setSquare(fieldID);
				
				if (!emptySquares.contains(fieldID)) {
					int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(rgbBoard, i, j);
					//int bgcolor_avg = (int) MatrixUtils.calculateColorStats(squareMatrix).getEntropy();
					
					MatrixUtils.PatternMatchingData bestPatternData = new MatrixUtils.PatternMatchingData();
					bestPatternData.x = 0;
					bestPatternData.y = 0;
					bestPatternData.size = squareMatrix.length;
					//ImageHandlerSingleton.getInstance().printInfo(squareMatrix, bestPatternData, "" + fieldID + "_square");
					
					Set<Integer> pidsToSearch = new HashSet<Integer>();
					//pidsToSearch.add(Constants.PID_NONE);
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
					
					List<Color> bgcolors = new ArrayList<Color>();
					//bgcolors.add(bgcolor_avg);
					bgcolors.add((file + rank) % 2 == 0 ? bgcolorsOfSquares.getFirst() : bgcolorsOfSquares.getSecond());
					
					ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData
						= getPID(squareMatrix, bgcolors, pidsToSearch, fieldID);
					pids[fieldID] = pidAndData.getFirst();
					MatrixUtils.PatternMatchingData data = pidAndData.getSecond();
					result.totalDelta += data.delta;
					//if (pids[fieldID] != Constants.PID_NONE) {
					countPIDs++;
					//}
				}
				countAll++;
				if (matchingInfo != null) matchingInfo.setCurrentPhaseProgress(countAll / (double) 64);
			}
		}
		
		result.totalDelta = result.totalDelta / (double) (countPIDs);
		//result.totalDelta *= boardProperties.getSquareSize() * Math.sqrt(boardProperties.getSquareSize());
		
		return new ResultPair<String, MatchingStatistics> (BoardUtils.createFENFromPIDs(pids), result);
	}
	
	
	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][][] rgbSquareMatrix,
			List<Color> bgcolors, Set<Integer> pids, int fieldID) {
		
		MatrixUtils.PatternMatchingData bestData = null;
		int bestPID = -1;
		
		int counter = 0;
		
		int maxSize = rgbSquareMatrix.length;
		int startSize = (int) (SIZE_DELTA_PERCENT_START * maxSize);
		int endSize = (int) (SIZE_DELTA_PERCENT_END * maxSize);
		
		for (int size = startSize; size <= endSize; size++) {
			
			MatrixUtils.PatternMatchingData curData_best  = null;
			
			for (Integer pid : pids) {
				
				if (pid == Constants.PID_NONE && size != endSize) {
					continue;
				}
				
				for (int i = 0; i < bgcolors.size(); i++) {
					
					Color bgcolor = bgcolors.get(i);
					
					int[][][] rgbPattern = (int[][][]) (pid == Constants.PID_NONE ?
							createSquareImage(bgcolor, size)
							: ImageHandlerSingleton.getInstance().convertToRGBMatrix(ImageHandlerSingleton.getInstance().createPieceImage_RGB(pid, bgcolor, size)));
					
							MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(rgbSquareMatrix, rgbPattern);
					
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
		
		//ImageHandlerSingleton.getInstance().printInfo(graySquareMatrix, bestData, "" + fieldID + "_matching");
		
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	private int[][][] createSquareImage(Color bgcolor, int size) {
		
		int[][][] result = new int[size][size][3];
		for (int i = 0; i < size; i++){
			for (int j = 0; j < size; j++){
				
				result[i][j][0] = bgcolor.red;
				result[i][j][1] = bgcolor.green;
				result[i][j][2] = bgcolor.blue;
			}
		}

		return result;
	}
}
