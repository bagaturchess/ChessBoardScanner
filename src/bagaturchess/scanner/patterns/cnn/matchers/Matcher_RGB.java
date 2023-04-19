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


public class Matcher_RGB extends Matcher_Base {


	protected NetworkModel networkModel;
	protected Object network;
	
	private boolean invertInput;
	
	
	public Matcher_RGB(BoardProperties _imageProperties, String _displayName, InputStream netStream) throws ClassNotFoundException, FileNotFoundException, IOException {
		this(_imageProperties, _displayName, netStream, false);
	}
	
	
	public Matcher_RGB(BoardProperties _imageProperties, String _displayName, InputStream netStream, boolean _invertInput) throws ClassNotFoundException, FileNotFoundException, IOException {
		
		super(_imageProperties, _displayName);
		
		networkModel = ProviderSwitch.getInstance().create(3, netStream, _imageProperties.getImageSize() / 8);
		network = networkModel.getNetwork();
		
		invertInput = _invertInput;
	}
	
	
	public ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) throws IOException {
		
		int[][][] rgbBoard = (int[][][]) boardMatrix;
		
		if (rgbBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("rgbBoard.length=" + rgbBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		
		MatchingData matchingData = buildMatchingData(rgbBoard, matchingInfo);
		
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
	
	
	private MatchingData buildMatchingData(int[][][] rgbBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (rgbBoard.length != boardProperties.getImageSize()) {
			throw new IllegalStateException("rgbBoard.length=" + rgbBoard.length + ", boardProperties.getImageSize()=" + boardProperties.getImageSize());
		}
		
		MatchingData result = new MatchingData();
		
		if (matchingInfo != null) matchingInfo.setPhaseName(getDisplayName());
		
		int countPIDs = 0;
		for (int i = 0; i < rgbBoard.length; i += rgbBoard.length / 8) {
			for (int j = 0; j < rgbBoard.length; j += rgbBoard.length / 8) {
				
				int file = i / (rgbBoard.length / 8);
				int rank = j / (rgbBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (matchingInfo != null) matchingInfo.setSquare(fieldID);
				
				int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(rgbBoard, i, j);
				
				ResultPair<Integer, MatrixUtils.PatternMatchingData> pidAndData = getPID(squareMatrix);
				result.pieceIDs[fieldID] = pidAndData.getFirst();
				result.squareData[fieldID] = pidAndData.getSecond();
				
				countPIDs++;
				if (matchingInfo != null) matchingInfo.setCurrentPhaseProgress(countPIDs / (double) 64);
			}
		}
		
		return result;
	}


	private ResultPair<Integer, MatrixUtils.PatternMatchingData> getPID(int[][][] rgbSquareMatrix) throws IOException {
		
		MatrixUtils.PatternMatchingData bestData = new MatrixUtils.PatternMatchingData();
		
		bestData.size = rgbSquareMatrix.length;
		
		bestData.delta = 0;
		
		
		if (invertInput) {
			
			rgbSquareMatrix = MatrixUtils.invertImage(rgbSquareMatrix);
		}
		
		
		float[][][] input = (float[][][]) networkModel.createInput(rgbSquareMatrix);
		
		networkModel.setInputs(input);
		
		float[] output = networkModel.feedForward();
		
		
		//If some values are NaN, than make them 0.
		for (int j = 0; j < output.length; j++) {
			
    		//System.out.println("output[j]=" + output[j]);
    		
			if (Float.isNaN(output[j])) {
				
				output[j] = 0;
			}
			
			if (output[j] < 0) {
				
				output[j] = 0;
			}
			
			if (output[j] > 1) {
				
				output[j] = 1;
			}
		}
		
		
		//Find best match
		int bestPID = -1;
		
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
