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
package bagaturchess.scanner.cnn.impl_deepnetts;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel;
import bagaturchess.scanner.common.BoardUtils;
import bagaturchess.scanner.common.MatrixUtils;


public class BoardScanner_Gray extends BoardScanner {
	
	
	public BoardScanner_Gray(NetworkModel networkModel) throws ClassNotFoundException, IOException {
		super(networkModel);
	}
	
	
	public String scan(Object imageObj) {
		
		int[][] grayImage = (int[][]) imageObj;
		
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayImage);
		
		int[] pids = new int[64];
		for (int i = 0; i < grayImage.length; i += grayImage.length / 8) {
			for (int j = 0; j < grayImage.length; j += grayImage.length / 8) {
				int file = i / (grayImage.length / 8);
				int rank = j / (grayImage.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				pids[fieldID] = Constants.PID_NONE;
				if (!emptySquares.contains(fieldID)) {
					int pid = getPID(grayImage, i, j, fieldID);
					pids[fieldID] = pid;
				}
			}
		}
		
		return BoardUtils.createFENFromPIDs(pids);
	}
	
	
	private int getPID(int[][] matrix, int i1, int j1, int filedID) {
		
		int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		networkModel.setInputs(networkModel.createInput(squareMatrix));
		
		network.forward();
		float[] output = network.getOutput();
		
		float maxValue = 0;
		int maxIndex = 0;
		for (int j = 0; j < output.length; j++) {
			if (j == 0 || j == 13) {//empty square
				continue;
			}
			if (maxValue < output[j]) {
				maxValue = output[j];
				maxIndex = j;
			}
		}
		
		int pid = (maxIndex == 13 ? Constants.PID_NONE : maxIndex);
		
		return pid;
	}
	
	
	@Override
	public double getAccumulatedProbability(Object image) {
		
		int[][] grayImage = (int[][]) image;
		
		Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayImage);
		
		double maxProbability = 0;
		List<Double> probs = new ArrayList<Double>();
		for (int i = 0; i < grayImage.length; i += grayImage.length / 8) {
			for (int j = 0; j < grayImage.length; j += grayImage.length / 8) {
				int file = i / (grayImage.length / 8);
				int rank = j / (grayImage.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				if (!emptySquares.contains(fieldID)) {
					double prob = getMaxProbability(grayImage, i, j, fieldID);
					probs.add(prob);
					if (maxProbability < prob) {
						maxProbability = prob;
					}
				}
			}
		}
		
		double probability = 0;
		
		for (Double prob: probs) {
			probability += prob;// / maxProbability;
		}
		
		probability = probability / (double) (65 - emptySquares.size());
		
		return probability;
	}
	
	
	private double getMaxProbability(int[][] matrix, int i1, int j1, int filedID) {
		
		int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		networkModel.setInputs(networkModel.createInput(squareMatrix));
		
		network.forward();
		float[] output = network.getOutput();
		
		double maxValue = 0;
		for (int j = 0; j < output.length; j++) {
			if (j == 0 || j == 13) {//empty square
				continue;
			}
			if (maxValue < output[j]) {
				maxValue = output[j];
			}
		}
		
		return maxValue;
	}
}
