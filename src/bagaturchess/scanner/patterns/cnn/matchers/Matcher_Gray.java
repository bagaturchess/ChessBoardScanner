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
package bagaturchess.scanner.patterns.cnn.matchers;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;
import bagaturchess.scanner.patterns.api.Matcher_Base;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class Matcher_Gray extends Matcher_Base {


	protected NetworkModel networkModel;
	protected Object network;
	
	
	public Matcher_Gray(BoardProperties _imageProperties, String _displayName, InputStream netStream) throws ClassNotFoundException, FileNotFoundException, IOException {
		
		super(_imageProperties, _displayName);
		
		networkModel = ProviderSwitch.getInstance().create(1, netStream, _imageProperties.getImageSize() / 8);
		network = networkModel.getNetwork();
	}
	
	
	public ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) throws IOException {
		
		int[][] grayBoard = (int[][]) boardMatrix;
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + grayBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		
		MatchingData matchingData = buildMatchingData(grayBoard, matchingInfo);
		
		int[] pids = new int[64];
		
		double[] deltas = new double[64];
		for (int squareID = 0; squareID < 64; squareID++) {
			deltas[squareID] = matchingData.squareData[squareID].delta;
			pids[squareID] = matchingData.pieceIDs[squareID];
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
	
	
	private MatchingData buildMatchingData(int[][] grayBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (grayBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("grayBoard.length=" + grayBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingData result = new MatchingData();
		
		if (matchingInfo != null) matchingInfo.setPhaseName(getDisplayName());
		
		int countPIDs = 0;
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (matchingInfo != null) matchingInfo.setSquare(fieldID);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				
				ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData = getPID(squareMatrix);
				result.pieceIDs[fieldID] = pidAndData.getFirst();
				result.squareData[fieldID] = pidAndData.getSecond();
				
				countPIDs++;
				if (matchingInfo != null) matchingInfo.setCurrentPhaseProgress(countPIDs / (double) 64);
			}
		}
		
		return result;
	}


	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][] graySquareMatrix) throws IOException {
		
		MatrixUtils.PatternMatchingData bestData = new MatrixUtils.PatternMatchingData();
		int bestPID = -1;
		
		float[][] input = (float[][]) networkModel.createInput(graySquareMatrix);
		networkModel.setInputs(input);
		float[] output = networkModel.feedForward();
		
		bestData.size = graySquareMatrix.length;
		bestData.delta = 0;
		int SKIP_EMPTY_FIELD = 0; //1
        for (int j = SKIP_EMPTY_FIELD; j < output.length; j++) {
        	if (output[j] >= bestData.delta) {
        		bestData.delta = output[j];
        		bestPID = j;
        	}
        }
        
		return new ResultPair<Integer, MatrixUtils.PatternMatchingData>(bestPID, bestData);
	}
	
	
	private static class MatchingData {
		private int[] pieceIDs = new int[64];
		private MatrixUtils.PatternMatchingData[] squareData = new MatrixUtils.PatternMatchingData[64];
	}
}
