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

import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.utils.ScannerUtils;

import deepnetts.data.ExampleImage;


public class ProbabilitiesCalculator_RGB extends ProbabilitiesCalculator {
	
	
	public ProbabilitiesCalculator_RGB(NetworkModel networkModel) throws ClassNotFoundException, IOException {
		super(networkModel);
	}


	@Override
	public double getAccumulatedProbability(Object image) {
		
		int[][][] rgbImage = (int[][][]) image;
		
		Set<Integer> emptySquares = new HashSet<Integer>(); //MatrixUtils.getEmptySquares(rgbImage);
		
		double maxProbability = 0;
		List<Double> probs = new ArrayList<Double>();
		for (int i = 0; i < rgbImage.length; i += rgbImage.length / 8) {
			for (int j = 0; j < rgbImage.length; j += rgbImage.length / 8) {
				int file = i / (rgbImage.length / 8);
				int rank = j / (rgbImage.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				if (!emptySquares.contains(fieldID)) {
					
					double prob = getMaxProbability(rgbImage, i, j, fieldID);
					//System.out.println("prob=" + prob);
					
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
	
	
	private double getMaxProbability(int[][][] matrix, int i1, int j1, int filedID) {
		
		//networkModel.setInputs(networkModel.createInput(squareMatrix));
		//float[] output = networkModel.feedForward();
		
		int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		ExampleImage image = new ExampleImage(ScannerUtils.createRGBImage(squareMatrix));
		
		//TODO: share instance in this object. Not creating it on each call.
		/*imageClassifier = new ImageClassifierNetwork((ConvolutionalNetwork) network);
		
		int[][][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(matrix, i1, j1);
		
		Map<String, Float> results = imageClassifier.classify(ScannerUtils.createRGBImage(squareMatrix));
		
		float[] output = new float[13];
		//System.out.println("NEW");
		for (String key: results.keySet()) {
			
			Float value = results.get(key);
			//System.out.println(key + " " + value);
			
			output[Integer.parseInt(key)] = value;
		}*/
		
		
		networkModel.setInputs(image.getInput());
		float[] probabilities = networkModel.feedForward();
		
		
		double maxValue = 0;
		for (int j = 0; j < probabilities.length; j++) {
			if (j == 0 || j == 13) {//empty square
				continue;
			}
			if (maxValue < probabilities[j]) {
				maxValue = probabilities[j];
			}
		}
		
		return maxValue;
	}
}
