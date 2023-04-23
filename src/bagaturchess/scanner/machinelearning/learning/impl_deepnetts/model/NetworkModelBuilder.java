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

import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;


public class NetworkModelBuilder {
	
	
	public static final ConvolutionalNetwork build(int square_size, int count_labels, int count_layers, int convolution_filter_size, int size_fully_connected_layer) throws ClassNotFoundException, IOException {
		
		return build(square_size, count_labels, count_layers, convolution_filter_size, 2, 2, size_fully_connected_layer);
	}
	
	public static final ConvolutionalNetwork build(int square_size, int count_labels, int count_layers, int convolution_filter_size, int maxpooling_filter_size, int maxpooling_filter_stride, int size_fully_connected_layer) throws ClassNotFoundException, IOException {

		ConvolutionalNetwork neuralNet = null;

		System.out.println("Creating neural network ...");
			
		switch (count_layers) {
			
			case 1:
				
				neuralNet =  ConvolutionalNetwork.builder()
                .addInputLayer(square_size, square_size, 3)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addFullyConnectedLayer(size_fully_connected_layer)
                .addOutputLayer(count_labels, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.TANH)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(777)
                .build();
				
				break;
				
			case 2:
				
				neuralNet =  ConvolutionalNetwork.builder()
                .addInputLayer(square_size, square_size, 3)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addFullyConnectedLayer(size_fully_connected_layer)
                .addOutputLayer(count_labels, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.TANH)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(777)
                .build();
				
				break;
				
			case 3:
				
				neuralNet =  ConvolutionalNetwork.builder()
                .addInputLayer(square_size, square_size, 3)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addConvolutionalLayer(3, convolution_filter_size, convolution_filter_size)
                .addMaxPoolingLayer(maxpooling_filter_size, maxpooling_filter_stride)
                .addFullyConnectedLayer(size_fully_connected_layer)
                .addOutputLayer(count_labels, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.TANH)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(777)
                .build();
				
				break;
				
			default :
				
				throw new IllegalStateException("count_layers=" + count_layers);
		}
		
        System.out.println("Network created.");
		
		return neuralNet;
	}
}
