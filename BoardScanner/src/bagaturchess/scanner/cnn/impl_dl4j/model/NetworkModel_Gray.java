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
package bagaturchess.scanner.cnn.impl_dl4j.model;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import bagaturchess.scanner.cnn.model.NetworkModel;


public class NetworkModel_Gray extends NetworkModel<MultiLayerNetwork> {
	
	
	private int squareSize;
	
	
	public NetworkModel_Gray(InputStream networkFileStream, int _squareSize) throws ClassNotFoundException, IOException {
		
		super();
		
		squareSize = _squareSize;
		
		if (networkFileStream != null) {
			
			System.out.println("Loading network ...");
			network = ModelSerializer.restoreMultiLayerNetwork(networkFileStream);
			System.out.println("Network loaded.");
			
		} else {
			
			System.out.println("Creating network ...");
			
			//Create network
	        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
	                //.seed(rngseed)
	                .updater(new Adam.Builder().learningRate(0.00001).build())
	        		//.updater(new Nesterovs(0.00000000000000001, 0))
	        		//.updater(new RmsProp(0.00000000000000001))
	                .activation(Activation.TANH)
	                //.activation(Activation.SIGMOID)
	                //.weightInit(WeightInit.XAVIER)
	                .list()
	                .layer(new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[]{0, 0}).name("conv1").nIn(1).nOut(2 * squareSize).biasInit(0).build())
	                .layer(new SubsamplingLayer.Builder(new int[] {2, 2}, new int[] {2, 2}).name("maxpool1").build())
	                .layer(new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[]{0, 0}).name("conv2").nIn(2 * squareSize).nOut(squareSize / 2).biasInit(0).build())
	                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
	                        .nOut(13)
	                        .activation(Activation.SOFTMAX)
	                        .build())
	                .setInputType(InputType.convolutional(squareSize, squareSize, 1))
	                .build();
	        
	        
	        network = new MultiLayerNetwork(conf);
			
	        network.init();
	        
			System.out.println("Network created.");	
		}
		
        network.setListeners(new ScoreIterationListener(100));
	}
	
	
	@Override
	public Object createInput(Object image) {
		
		float[][] result = convertInt2Float((int[][])image);
		
		return result;
	}
	
	
	@Override
	public void setInputs(Object input) {
		/*if (network.getInput() != null) {
			network.getInput().cleanup();
		}*/
		network.setInput(Nd4j.create((float[][])input).reshape(1, 1, squareSize, squareSize));
	}
	
	
	@Override
	public float[] feedForward() {
		List<INDArray> outputs = network.feedForward();
		INDArray output = outputs.get(outputs.size() - 1);
		return output.toFloatVector();
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
