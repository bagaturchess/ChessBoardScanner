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
package bagaturchess.scanner.machinelearning.classification.probabilities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.common.MatrixUtils;


public class ProbabilitiesCalculator_Gray extends ProbabilitiesCalculator {
	
	
	public ProbabilitiesCalculator_Gray(NetworkModel networkModel) throws ClassNotFoundException, IOException {
		super(networkModel);
	}
	
	
	@Override
	public double getAccumulatedProbability(Object image, IMatchingInfo matchingInfo, VarStatistic stats) {
		
		int[][] grayImage = (int[][]) image;
		
		Set<Integer> emptySquares = new HashSet<Integer>();
		//Set<Integer> emptySquares = MatrixUtils.getEmptySquares_Heuristic2(grayImage);
		
		double maxProbability = 0;
		
		List<Double> probs = new ArrayList<Double>();
		
		for (int i = 0; i < grayImage.length; i += grayImage.length / 8) {
			
			for (int j = 0; j < grayImage.length; j += grayImage.length / 8) {
				
				int file = i / (grayImage.length / 8);
				int rank = j / (grayImage.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				if (!emptySquares.contains(fieldID)) {
					
					double max_prob = getMaxProbability(grayImage, i, j, fieldID, stats);
					
					probs.add(max_prob);
					
					if (maxProbability < max_prob) {
						
						maxProbability = max_prob;
					}
				}
			}
		}
		
		double probability = 0;
		
		for (Double prob: probs) {
			probability += prob;
			//probability += maxProbability;
		}
		
		probability = probability / (double) (65 - emptySquares.size());
		
		return probability;
	}
	
	
	private double getMaxProbability(int[][] matrix, int i1, int j1, int filedID, VarStatistic stats) {
		
		int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		networkModel.setInputs(networkModel.createInput(squareMatrix));
		
		float[] output = networkModel.feedForward();
		
		double maxValue = 0;

		for (int j = 0; j < output.length; j++) {
			
			if (j == 0 || j == 13) {//empty square
				
				//continue;
			}
			
			float cur_val = output[j];
			
			stats.addValue(cur_val, cur_val);
			
			if (maxValue < cur_val) {
				
				maxValue = cur_val;
			}
		}
		
		return maxValue;
	}
}
