package bagaturchess.scanner.cnn.dataset;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.cnn.impl_deepnetts.learning.ScannerDataSet;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_RGB;
import bagaturchess.scanner.cnn.model.NetworkModel;
import bagaturchess.scanner.common.BoardProperties;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.FileIO;


public class DatasetGenerator {
	
	
	private static BackpropagationTrainer trainer;
	private static ScannerDataSet dataset;
	
	private static NetworkModel<ConvolutionalNetwork> netmodel;
	
	private static long lastSave = System.currentTimeMillis();
	
	
	public static void main(String[] args) {
		
		try {
			
			String[] inputFiles = new String[] {
					
				/*"./res/cnn/chess.com/set1/input1.png",
				"./res/cnn/chess.com/set1/input2.png",
				"./res/cnn/chess.com/set1/input3.png",
				"./res/cnn/chess.com/set1/input4.png",
				"./res/cnn/chess.com/set1/input5.png",
				"./res/cnn/chess.com/set1/input6.png",
				"./res/cnn/chess.com/set1/input7.png",
				*/
					
				/*"./res/cnn/lichess.org/set1/input1.png",
				"./res/cnn/lichess.org/set1/input2.png",
				"./res/cnn/lichess.org/set1/input3.png",
				"./res/cnn/lichess.org/set1/input4.png",
				"./res/cnn/lichess.org/set1/input5.png",
				"./res/cnn/lichess.org/set1/input6.png",
				"./res/cnn/lichess.org/set1/input7.png",*/
				
				"./res/cnn/chess.com/set2/input1.png",
				"./res/cnn/chess.com/set2/input2.png",
				"./res/cnn/chess.com/set2/input3.png",
				"./res/cnn/chess.com/set2/input4.png",
				"./res/cnn/chess.com/set2/input5.png",
				"./res/cnn/chess.com/set2/input6.png",
				"./res/cnn/chess.com/set2/input7.png",
				"./res/cnn/chess.com/set2/input8.png",
				"./res/cnn/chess.com/set2/input9.png",
				"./res/cnn/chess.com/set2/input10.png",
				
				/*"./res/cnn/lichess.org/set2/input1.png",
				"./res/cnn/lichess.org/set2/input2.png",
				"./res/cnn/lichess.org/set2/input3.png",
				"./res/cnn/lichess.org/set2/input4.png",
				"./res/cnn/lichess.org/set2/input5.png",
				"./res/cnn/lichess.org/set2/input6.png",
				"./res/cnn/lichess.org/set2/input7.png",
				"./res/cnn/lichess.org/set2/input8.png",
				"./res/cnn/lichess.org/set2/input9.png",
				"./res/cnn/lichess.org/set2/input10.png",*/
				
				/*"./res/cnn/chess24.com/set2/input1.png",
				"./res/cnn/chess24.com/set2/input2.png",
				"./res/cnn/chess24.com/set2/input3.png",
				"./res/cnn/chess24.com/set2/input4.png",
				"./res/cnn/chess24.com/set2/input5.png",
				"./res/cnn/chess24.com/set2/input6.png",
				"./res/cnn/chess24.com/set2/input7.png",
				"./res/cnn/chess24.com/set2/input8.png",
				"./res/cnn/chess24.com/set2/input9.png",
				"./res/cnn/chess24.com/set2/input10.png",
				"./res/cnn/chess24.com/set2/input11.png",*/
			};
			
			
			BoardProperties boardProperties = new BoardProperties(256);
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs_RGB(boardProperties, inputFiles, "./datasets_deepnetts/dataset_gen_test/");
			
			final List<Object> images = new ArrayList<Object>();
			final List<Integer> pids = new ArrayList<Integer>();
			
			for (int i = 0; i < pairs.length; i++) {
				images.addAll(pairs[i].getImages());
				pids.addAll(pairs[i].getPIDs());
			}
			
			
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
