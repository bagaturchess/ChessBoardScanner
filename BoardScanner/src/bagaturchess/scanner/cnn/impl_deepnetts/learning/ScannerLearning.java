package bagaturchess.scanner.cnn.impl_deepnetts.learning;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.cnn.dataset.DataSetInitPair;
import bagaturchess.scanner.cnn.dataset.DataSetUtils;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.model.NetworkModel;
import bagaturchess.scanner.common.BoardProperties;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.FileIO;


public class ScannerLearning {
	
	
	private static final String NET_FILE = "scanner.chesscom1.bin";
	
	private static BackpropagationTrainer trainer;
	private static ScannerDataSet dataset;
	
	private static NetworkModel<ConvolutionalNetwork> netmodel;
	
	private static long lastSave = System.currentTimeMillis();
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			netmodel = new NetworkModel_Gray((new File(NET_FILE)).exists() ? new FileInputStream(NET_FILE) : null, boardProperties.getSquareSize());
			
			String[] inputFiles = new String[] {
				"./data/tests/cnn/chess.com/set1/input1.png",
				"./data/tests/cnn/chess.com/set1/input2.png",
				"./data/tests/cnn/chess.com/set1/input3.png",
				"./data/tests/cnn/chess.com/set1/input4.png",
				"./data/tests/cnn/chess.com/set1/input5.png",
				"./data/tests/cnn/chess.com/set1/input6.png",
				"./data/tests/cnn/chess.com/set1/input7.png",
				
					
				/*"./data/tests/cnn/lichess.org/set1/input1.png",
				"./data/tests/cnn/lichess.org/set1/input2.png",
				"./data/tests/cnn/lichess.org/set1/input3.png",
				"./data/tests/cnn/lichess.org/set1/input4.png",
				"./data/tests/cnn/lichess.org/set1/input5.png",
				"./data/tests/cnn/lichess.org/set1/input6.png",
				"./data/tests/cnn/lichess.org/set1/input7.png",
				*/
			};
			
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs(boardProperties, inputFiles);
			
			final List<Object> images = new ArrayList<Object>();
			final List<Integer> pids = new ArrayList<Integer>();
			
			for (int i = 0; i < pairs.length; i++) {
				images.addAll(pairs[i].getImages());
				pids.addAll(pairs[i].getPIDs());
			}
			
			
			dataset = new ScannerDataSet();
			for (int i = 0; i < images.size(); i++) {
				Object networkInput = netmodel.createInput(images.get(i));
				float[] networkOutput = new float[14];
				networkOutput[pids.get(i)] = 1;
				dataset.addItem(networkInput, networkOutput);
			}
			
			ConvolutionalNetwork network = netmodel.getNetwork();
			
			trainer = new BackpropagationTrainer(network);
			
			trainer.setLearningRate(1f);
	        
	        trainer.setBatchMode(true);
	        trainer.setBatchSize(images.size());
	        
	        trainer.setMaxEpochs(100000);
	        
	        trainer.addListener(new TrainingListener() {
	        	
	        	
	        	private int iteration = 0;
	        	private long startTime = System.currentTimeMillis();
	        	
	        	
				@Override
				public void handleEvent(TrainingEvent event) {
					
					if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
						
						int success = 0;
						int failure = 0;
						for (int i = 0; i < images.size(); i++) {
							
							Object networkInput = netmodel.createInput(images.get(i));
							netmodel.setInputs(networkInput);
							network.forward();
							float[] actual_output = network.getOutput();
							
							float maxValue = 0;
							int maxIndex = 0;
							for (int j = 0; j < actual_output.length; j++) {
								if (maxValue < actual_output[j]) {
									maxValue = actual_output[j];
									maxIndex = j;
								}
							}
							
							if (maxIndex == pids.get(i)) {
								success++;
							} else {
								failure++;
							}
						}
						
						if (!Float.isNaN(event.getSource().getTrainingLoss())
								&& !Float.isInfinite(event.getSource().getTrainingLoss())) {
							try {
								long now = System.currentTimeMillis();
								if (now > lastSave + 10000) {
									FileIO.writeToFile(network, NET_FILE);
									lastSave = now;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						System.out.println("End iteration " + iteration
								+ ": Time " + (System.currentTimeMillis() - startTime)
								+ "ms, Training loss is " + event.getSource().getTrainingLoss()
								+ ", Success is " + success / (float)(success + failure)
						);
						
						iteration++;
						
					} else if (event.getType().equals(TrainingEvent.Type.ITERATION_FINISHED)) {
						//System.out.println("done");
					}
				}
			});
	        
	        
	        trainer.train(dataset);
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
