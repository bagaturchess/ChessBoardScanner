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
package bagaturchess.scanner.machinelearning.learning.impl_deepnetts.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import bagaturchess.scanner.machinelearning.model.NetworkModel;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;


public class NetworkModel_Gray extends NetworkModel<ConvolutionalNetwork> {
	
	
	public NetworkModel_Gray(InputStream networkFileStream, int squareSize) throws ClassNotFoundException, IOException {
		
		super();
		
		if (networkFileStream != null) {
			System.out.println("Loading network ...");
			ObjectInputStream ois = new ObjectInputStream(networkFileStream);
			network = (ConvolutionalNetwork) ois.readObject();
			System.out.println("Network loaded.");
		} else {
			
			if (true) {
				
				throw new IllegalStateException();
			}
			
			System.out.println("Creating network ...");
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(squareSize, squareSize, 1)
	                .addConvolutionalLayer(3, 3, 3)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(3, 3, 3)
	                .addMaxPoolingLayer(2, 2)
	                .addFullyConnectedLayer(8 * 13)
	                .addOutputLayer(13, ActivationType.SOFTMAX)
	                .hiddenActivationFunction(ActivationType.TANH)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(777)
	                .build();
			System.out.println("Network created.");	
		}
	}
	
	
	@Override
	public Object createInput(Object image) {
		
		float[][] result = convertInt2Float((int[][])image);
		
		return result;
	}
	
	
	@Override
	public void setInputs(Object input) {
		//network.setInput(new Tensor((float[][])input)); //TODO: Fix. See DeepNetts RGB model.
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public float[] feedForward() {
		network.forward();
		float[] output = network.getOutput();
		return output;
	}
	
	
	private static float[][] convertInt2Float(int[][] array) {
		float[][] result = new float[array.length][array.length];
		for (int i = 0 ; i < array.length; i++) {
			for (int j = 0 ; j < array.length; j++) {
				result[i][j] = array[i][j];
			}
		}
		return result;
	}
}
