package bagaturchess.scanner.machinelearning.learning.impl_deepnetts;


import deepnetts.core.DeepNetts;
import deepnetts.data.ImageSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.util.DeepNettsException;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import javax.visrec.ml.eval.EvaluationMetrics;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.FileIO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ScannerLearning_Edition_Pro13 implements Runnable {
	
	
	private String INPUT_DIR_NAME;
	private String OUTPUT_FILE_NAME;
	private float LEARNING_RATE;
	
	
    // download data set and set these paths
	private String labelsFile;
	private String trainingFile;
    
    
	private int imageWidth = 32;
	private int imageHeight = 32;
    
	
	private boolean finished = false;
	
	
    private static final Logger LOGGER = LogManager.getLogger(DeepNetts.class.getName());
    
    
    private ScannerLearning_Edition_Pro13(String _INPUT_DIR_NAME, String _OUTPUT_FILE_NAME, float _LEARNING_RATE) {
    	
    	INPUT_DIR_NAME = _INPUT_DIR_NAME;
    	OUTPUT_FILE_NAME = _OUTPUT_FILE_NAME;
    	LEARNING_RATE = _LEARNING_RATE;
    	
    	labelsFile = INPUT_DIR_NAME + "labels.txt";
    	trainingFile = INPUT_DIR_NAME + "index.txt";
    }
    
    
    public static void main(String[] args) {
    	
        try {
        	
        	List<Runnable> learningTasks = new ArrayList<Runnable>();
        	
        	/*learningTasks.add(new ScannerLearning("./datasets_deepnetts/dataset_books_set_1_extended/",
													"dnet_books_set_1_extended.dnet",
													0.01f
								)
        			);
			
        	learningTasks.add(new ScannerLearning("./datasets_deepnetts/dataset_chess24com_set_1_extended/",
													"dnet_chess24com_set_1_extended.dnet",
													0.01f
								)
					);*/
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_1_extended/",
													"dnet_chesscom_set_1_extended.dnet",
													0.001f
								)
					);
        	
        	/*learningTasks.add(new ScannerLearning("./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
													"dnet_lichessorg_set_1_extended.dnet",
													0.01f
								)
					);*/
        	
			ExecutorService executor = Executors.newFixedThreadPool(learningTasks.size());
			
			for (Runnable learning: learningTasks) {
				executor.execute(learning);
			}
			

		} catch (Throwable t) {
			
			t.printStackTrace();
		}
    }
    
    
    public void run() {
    	
    	LOGGER.info("INPUT DIR: " + INPUT_DIR_NAME);
    	LOGGER.info("OUTPUT FILE: " + OUTPUT_FILE_NAME);
    	
        LOGGER.info("Training convolutional network");
        LOGGER.info("Loading images...");
        
        // create a data set from images and labels
        ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
        
        //This is important: with gray scale images, the recognition of chess board squares works better!
        //imageSet.setGrayscale(true);
        
        imageSet.loadLabels(new File(labelsFile));
        
        try {
        	
            imageSet.loadImages(new File(trainingFile));

            int labelsCount = imageSet.getLabelsCount();
            
            //ImageSet[] imageSets = imageSet.split(0.7, 0.3);

            final ConvolutionalNetwork neuralNet = getNewOrLoadNetwork(new File(OUTPUT_FILE_NAME), labelsCount);
            
            LOGGER.info("Training neural network ...");

            
            Thread saverThread = new Thread(new Runnable() {
    			@Override
    			public void run() {
    				try {
    					while (!finished) {
    				        Thread.currentThread().sleep(60000);
    				        // Save trained network to file
    				        FileIO.writeToFile(neuralNet, OUTPUT_FILE_NAME);
    				        LOGGER.info("Network saved as " + OUTPUT_FILE_NAME);
    					}
    				} catch(Throwable t) {
    					t.printStackTrace();
    				}
    			}
    		});
            saverThread.start();
            
            
            // create a trainer and train network
            BackpropagationTrainer trainer = neuralNet.getTrainer();
            
            trainer.setLearningRate(LEARNING_RATE)
                    .setMaxError(0.01f)
                    .setMaxEpochs(10000);
            
            
            trainer.train(imageSet);
            
            finished = true;
            
            // Test trained network
            /*ClassifierEvaluator evaluator = new ClassifierEvaluator();
            evaluator.evaluate(neuralNet, imageSets[1]);
            
            LOGGER.info("------------------------------------------------");
            LOGGER.info("Classification performance measure" + System.lineSeparator());
            LOGGER.info("TOTAL AVERAGE");
            LOGGER.info(evaluator.getMacroAverage());
            LOGGER.info("By Class");
            Map<String, EvaluationMetrics> byClass = evaluator.getPerformanceByClass();
            
            Set<Map.Entry<String, EvaluationMetrics>> entrySet = byClass.entrySet();
            for (Map.Entry<String, EvaluationMetrics> curEntry : entrySet) {
                LOGGER.info("Class " + curEntry.getKey() + ":");
                LOGGER.info(curEntry.getValue());
                LOGGER.info("----------------");
            }

            ConfusionMatrix cm = evaluator.getConfusionMatrix();
            LOGGER.info(cm.toString());*/
            
        } catch (Throwable t) {
        	t.printStackTrace();
        }
    }


	private ConvolutionalNetwork getNewOrLoadNetwork(File netFile, int labelsCount) {
		
		ConvolutionalNetwork neuralNet = null;
		
		/*if (netFile.exists()) {
			try {
				System.out.println("Loading network ...");
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(netFile));
				neuralNet = (ConvolutionalNetwork) ois.readObject();
				System.out.println("Network loaded.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		if (neuralNet == null) {
			System.out.println("Creating neural network ...");
	        neuralNet =  ConvolutionalNetwork.builder()
	                .addInputLayer(imageWidth, imageHeight)
	                .addConvolutionalLayer(5, 5)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(5, 5)
	                .addMaxPoolingLayer(2, 2)
	                .addFullyConnectedLayer(64)
	                .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
	                .hiddenActivationFunction(ActivationType.TANH)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(777)
	                .build();
	        System.out.println("Network created.");
		}
		
		return neuralNet;
	}
}

