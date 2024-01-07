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
import java.util.List;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.common.MatrixUtils;


public class ProbabilitiesCalculator_Scores_RGB extends ProbabilitiesCalculator {
	
	
	public ProbabilitiesCalculator_Scores_RGB(NetworkModel networkModel) throws ClassNotFoundException, IOException {
		super(networkModel);
	}


	@Override
	public double getAccumulatedProbability(Object image, IMatchingInfo matchingInfo, VarStatistic stats) {
		
		int[][][] rgbImage = (int[][][]) image;
		
		List<Double> probs = new ArrayList<Double>();
		
		int count_w_pawns 	= 0;
		int count_w_knights = 0;
		int count_w_bishops = 0;
		int count_w_rooks 	= 0;
		int count_w_queens 	= 0;
		int count_w_king 	= 0;
		int count_b_pawns 	= 0;
		int count_b_knights = 0;
		int count_b_bishops = 0;
		int count_b_rooks 	= 0;
		int count_b_queens 	= 0;
		int count_b_king 	= 0;
		
		for (int i = 0; i < rgbImage.length; i += rgbImage.length / 8) {
			for (int j = 0; j < rgbImage.length; j += rgbImage.length / 8) {
				
				int file = i / (rgbImage.length / 8);
				int rank = j / (rgbImage.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				double[] prob_result = getMaxProbability(rgbImage, i, j, fieldID, stats);
				
				//System.out.println("prob_result=" + prob_result[0] + " " + prob_result[1]);
				
				double max_prob = prob_result[0];
				probs.add(max_prob);
				
				int piece_type = (int) prob_result[1];
				
				switch(piece_type) {
					
					case 1:
						count_w_pawns++;
						break;
					case 2:
						count_w_knights++;
						break;
					case 3:
						count_w_bishops++;
						break;
					case 4:
						count_w_rooks++;
						break;
					case 5:
						count_w_queens++;
						break;
					case 6:
						count_w_king++;
						break;
					case 7:
						count_b_pawns++;
						break;
					case 8:
						count_b_knights++;
						break;
					case 9:
						count_b_bishops++;
						break;
					case 10:
						count_b_rooks++;
						break;
					case 11:
						count_b_queens++;
						break;
					case 12:
						count_b_king++;
						break;
				}
			}
		}
		
		
		double scores = 1;
		
		if (count_w_king != 1) {
			
			scores -= 0.17;

			if (count_w_king > 1) {

				scores -= 0.01 * (count_w_king - 1);
			}
		}
		
		if (count_b_king != 1) {
			
			scores -= 0.17;

			if (count_b_king > 1) {

				scores -= 0.01 * (count_b_king - 1);
			}
		}
		
		if (count_w_queens > 1) {
			
			scores -= 0.01 * (count_w_queens - 1);
		}
		
		if (count_b_queens > 1) {
			
			scores -= 0.01 * (count_b_queens - 1);
		}
		
		if (count_w_rooks > 2) {
			
			scores -= 0.01 * (count_w_rooks - 2);
		}
		
		if (count_b_rooks > 2) {
			
			scores -= 0.01 * (count_b_rooks - 2);
		}
		
		if (count_w_bishops > 2) {
			
			scores -= 0.01 * (count_w_bishops - 2);
		}
		
		if (count_b_bishops > 2) {
			
			scores -= 0.01 * (count_b_bishops - 2);
		}
		
		if (count_w_knights > 2) {
			
			scores -= 0.01 * (count_w_knights - 2);
		}
		
		if (count_b_knights > 2) {
			
			scores -= 0.01 * (count_b_knights - 2);
		}
		
		if (count_w_pawns > 8) {
			
			scores -= 0.01 * (count_w_pawns - 8);
		}
		
		if (count_b_pawns > 8) {
			
			scores -= 0.01 * (count_b_pawns - 8);
		}
		
		
		double probability = 0;
		
		for (Double prob: probs) {
			probability += prob;
		}
		
		probability = probability / (double) 65;
		
		return scores * probability;
		//return scores;
	}
	
	
	private double[] getMaxProbability(int[][][] matrix, int i1, int j1, int filedID, VarStatistic stats) {
		
		int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		networkModel.setInputs(networkModel.createInput(squareMatrix));
		
		float[] output = networkModel.feedForward();
		
		double maxValue = 0;
		double index = -1;
		
		for (int j = 0; j < output.length; j++) {
			
			if (j == 0 || j == 13) {//empty square
				
				//continue;
			}
			
			float cur_val = output[j];
			
			stats.addValue(cur_val, cur_val);
			
			if (maxValue <= cur_val) {
				
				maxValue = cur_val;
				
				index = j;
			}
		}
		
		return new double[] {maxValue, index};
	}
}
