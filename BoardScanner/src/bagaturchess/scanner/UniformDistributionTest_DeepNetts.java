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
package bagaturchess.scanner;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.machinelearning.model.NetworkModel;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;


public class UniformDistributionTest_DeepNetts {
	
	
	public static void main(String[] args) {
		
		try {
			
			
			ProviderSwitch.MLFrameworkName = "deepnetts";
			
			
            List<String> netsNames = new ArrayList<String>();
            netsNames.add("cnn_lichessorg_set_1.dnet");
            netsNames.add("cnn_chesscom_set_1.dnet");
            netsNames.add("cnn_chess24com_set_1.dnet");
            netsNames.add("cnn_books_set_1.dnet");
            
			List<InputStream> netsStreams = new ArrayList<InputStream>();
			for (int i = 0; i < netsNames.size(); i++) {
				netsStreams.add(new FileInputStream(netsNames.get(i)));
			}
			
			BoardProperties boardProperties = new BoardProperties(256);
			
            List<NetworkModel> nets = new ArrayList<NetworkModel>();
            nets.add(ProviderSwitch.getInstance().create(3, netsStreams.get(0), boardProperties.getImageSize() / 8));
            nets.add(ProviderSwitch.getInstance().create(3, netsStreams.get(1), boardProperties.getImageSize() / 8));
            nets.add(ProviderSwitch.getInstance().create(3, netsStreams.get(2), boardProperties.getImageSize() / 8));
            nets.add(ProviderSwitch.getInstance().create(3, netsStreams.get(3), boardProperties.getImageSize() / 8));
            
			
            
            VarStatistic stats = new VarStatistic(false);
            
            
            for (int iteration = 0; iteration < 100; iteration++) {
            	
	            for (int i = 0; i < nets.size(); i++) {
	            	
	            	NetworkModel curModel = nets.get(i);
	            	
	            	int[][][] rgbImageMatrix = createRandomImage(boardProperties.getImageSize() / 8);
	            	
	            	Object inputs = curModel.createInput(rgbImageMatrix);
	            	
	            	curModel.setInputs(inputs);
	            	
	            	float[] output = curModel.feedForward();
	            	
	            	for (int j = 0; j < output.length; j++) {
	            		float prob = 100 * output[j];
	            		stats.addValue(prob, prob);
	            	}
	            }
	            
	            
	            System.out.println("Interation " + iteration + ", mean=" + stats.getEntropy() + ", stdev=" + stats.getDisperse());
            }
            
            
            System.exit(0);
            
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static final int[][][] createRandomImage(int size) {
		int[][][] result = new int[size][size][3];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result.length; j++) {
				result[i][j][0] = (int) (Math.random() * 255);
				result[i][j][1] = (int) (Math.random() * 255);
				result[i][j][2] = (int) (Math.random() * 255);
			}
		}
		return result;
	}
}
