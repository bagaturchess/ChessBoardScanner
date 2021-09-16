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
import deepnetts.util.Tensor;


public class NetworkModel_RGB extends NetworkModel<ConvolutionalNetwork> {
	
	
	public NetworkModel_RGB(InputStream networkFileStream, int squareSize) throws ClassNotFoundException, IOException {
		
		super();
		
		if (networkFileStream != null) {
			System.out.println("Loading network ...");
			ObjectInputStream ois = new ObjectInputStream(networkFileStream);
			network = (ConvolutionalNetwork) ois.readObject();
			System.out.println("Network loaded.");
		} else {
			System.out.println("Creating network ...");
			network =  ConvolutionalNetwork.builder()
	                .addInputLayer(squareSize, squareSize, 3)
	                .addConvolutionalLayer(5, 5, 64)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(5, 5, 16)
	                .addMaxPoolingLayer(2, 2)
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
		return convertInt2Float((int[][][])image);
	}
	
	
	@Override
	public void setInputs(Object input) {
		
		float[][][] pixels = (float[][][]) input;
		
		Tensor tensor = extractPixelColors(pixels, pixels.length);
		
		network.setInput(tensor);
	}
	
	
	@Override
	public float[] feedForward() {
		network.forward();
		float[] output = network.getOutput();
		return output;
	}
	
	
	private static float[][][] convertInt2Float(int[][][] array) {
		float[][][] result = new float[array.length][array.length][array[0][0].length];
		for (int i = 0 ; i < array.length; i++) {
			for (int j = 0 ; j < array[0].length; j++) {
				for (int k = 0 ; k < array[0][0].length; k++) {
					result[i][j][k] = array[i][j][k];
				}
			}
		}
		return result;
	}
	
	
    private Tensor extractPixelColors(float[][][] pixels, int size) {
    	
        float[] rgbVector = new float[size * size * 3];

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

            	float red = pixels[y][x][0];
            	float green = pixels[y][x][1];
            	float blue = pixels[y][x][2];
                
                rgbVector[y * size + x] = blue / 255.0f;
                rgbVector[size * size + y * size + x] = green / 255.0f;
                rgbVector[2 * size * size + y * size + x] = red / 255.0f;
            }
        }
        
        Tensor rgbTensor = new Tensor(size, size, 3, rgbVector);
        
        return rgbTensor;
    }    
}