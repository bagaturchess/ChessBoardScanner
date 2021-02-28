package bagaturchess.scanner.cnn.impl_dl4j.learning;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

import bagaturchess.scanner.cnn.dataset.DataSetInitPair;
import bagaturchess.scanner.cnn.dataset.DataSetUtils;
import bagaturchess.scanner.cnn.impl_dl4j.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.model.NetworkModel;
import bagaturchess.scanner.common.BoardProperties;


public class ScannerLearning {
	
	
	private static final String NET_FILE = "scanner.lichessorg1.bin";
	
	
	private static NetworkModel<MultiLayerNetwork> netmodel;
	private static DataSetIteratorImpl dataset;
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			netmodel = new NetworkModel_Gray((new File(NET_FILE)).exists() ? new FileInputStream(NET_FILE) : null, boardProperties.getSquareSize());
			
			String[] inputFiles = new String[] {
				/*"./data/tests/cnn/chess.com/set1/input1.png",
				"./data/tests/cnn/chess.com/set1/input2.png",
				"./data/tests/cnn/chess.com/set1/input3.png",
				"./data/tests/cnn/chess.com/set1/input4.png",
				"./data/tests/cnn/chess.com/set1/input5.png",
				"./data/tests/cnn/chess.com/set1/input6.png",
				"./data/tests/cnn/chess.com/set1/input7.png",
				*/
					
				"./data/tests/cnn/lichess.org/set1/input1.png",
				"./data/tests/cnn/lichess.org/set1/input2.png",
				"./data/tests/cnn/lichess.org/set1/input3.png",
				"./data/tests/cnn/lichess.org/set1/input4.png",
				"./data/tests/cnn/lichess.org/set1/input5.png",
				"./data/tests/cnn/lichess.org/set1/input6.png",
				"./data/tests/cnn/lichess.org/set1/input7.png",
				
			};
			
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs(boardProperties, inputFiles);
			
			final List<Object> images = new ArrayList<Object>();
			final List<Integer> pids = new ArrayList<Integer>();
			
			for (int i = 0; i < pairs.length; i++) {
				images.addAll(pairs[i].getImages());
				pids.addAll(pairs[i].getPIDs());
			}
			
			
			dataset = new DataSetIteratorImpl();
			for (int i = 0; i < images.size(); i++) {
				Object networkInput = netmodel.createInput(images.get(i));
				float[] networkOutput = new float[13];
				networkOutput[pids.get(i)] = 1;
				dataset.addEntry(
						new DataSet(
									Nd4j.create((float[][])networkInput).reshape(1, 1, boardProperties.getSquareSize(), boardProperties.getSquareSize()),
									Nd4j.create(networkOutput)
								)
						);
			}
			
			MultiLayerNetwork network = netmodel.getNetwork();
			
	        //Train network
			for (int e = 0; e < 100; e++) {
				dataset.reset();
				network.fit(dataset);
			}
			
			
			//Save network
			boolean saveUpdater = true;
			ModelSerializer.writeModel(network, new File(NET_FILE), saveUpdater);
	        
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
