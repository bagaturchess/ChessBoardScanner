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

import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.common.MatrixUtils;


public class ProbabilitiesCalculator_RGB_ChessPiecesProviderClassifier extends ProbabilitiesCalculator {
	
	
	public ProbabilitiesCalculator_RGB_ChessPiecesProviderClassifier(NetworkModel networkModel) throws ClassNotFoundException, IOException {
		
		super(networkModel);
		
		//System.out.println("networkModel=" + networkModel.getNetwork());
	}


	@Override
	public double[] getAccumulatedProbabilitiesByLabelIndex(Object image) {
		
		int[][][] rgbImage = (int[][][]) image;
		
		double[] result = null;
		
		for (int i = 0; i < rgbImage.length; i += rgbImage.length / 8) {
			for (int j = 0; j < rgbImage.length; j += rgbImage.length / 8) {
				
				//int file = i / (rgbImage.length / 8);
				//int rank = j / (rgbImage.length / 8);
				//int fieldID = 63 - (file + 8 * rank);

				int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(rgbImage, i, j);
				float[] probs = getProbabilities(squareMatrix);
				
				if (result == null) {
					result = new double[probs.length];
				}
				
				for (int k = 0; k < probs.length; k++) {
					result[k] += (probs[k] / 64.0f);
				}
			}
		}
		
		return result;
	}
	
	
	@Override
	public double getAccumulatedProbability(Object image) {
		throw new UnsupportedOperationException();
	}
	
	
	private float[] getProbabilities(int[][][] squareMatrix) {
		
		networkModel.setInputs(networkModel.createInput(squareMatrix));
		
		float[] output = networkModel.feedForward();
		
		return output;
	}
}
