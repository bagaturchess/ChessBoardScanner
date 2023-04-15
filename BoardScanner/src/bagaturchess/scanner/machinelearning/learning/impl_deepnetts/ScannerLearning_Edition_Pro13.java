package bagaturchess.scanner.machinelearning.learning.impl_deepnetts;


import deepnetts.core.DeepNetts;
import deepnetts.data.ImageSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.TrainingEvent;
import deepnetts.net.train.TrainingListener;
import deepnetts.util.DeepNettsException;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import javax.visrec.ml.eval.EvaluationMetrics;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.FileIO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ScannerLearning_Edition_Pro13 implements Runnable {
	

	private static final float MAX_ERROR_MEAN_CROSS_ENTROPY 	= 0.0001f;
	
	private static final float MAX_ERROR_MEAN_SQUARED_ERROR 	= MAX_ERROR_MEAN_CROSS_ENTROPY / 1000f;
	
	private static final float LEARNING_RATE_1 					= 1f;
	private static final float LEARNING_RATE_10 				= 0.1f;
	private static final float LEARNING_RATE_50 				= 0.02f;
	private static final float LEARNING_RATE_100 				= 0.01f;
	private static final float LEARNING_RATE_200 				= 0.005f;
	private static final float LEARNING_RATE_400 				= 0.0025f;
	private static final float LEARNING_RATE_800 				= 0.00125f;
	
	private static final float LEARNING_RATE_1K 				= 0.001f;
	private static final float LEARNING_RATE_2K 				= 0.0005f;
	private static final float LEARNING_RATE_4K 				= 0.00025f;
	private static final float LEARNING_RATE_8K 				= 0.000125f;
	private static final float LEARNING_RATE_10K 				= 0.000125f;
	private static final float LEARNING_RATE_16K 				= 0.0000625f;
	
	private static final float LEARNING_RATE_NN_UNIVERSAL 		= LEARNING_RATE_200;
	private static final float LEARNING_RATE_NN_BOOK_SET1 		= LEARNING_RATE_400;
	private static final float LEARNING_RATE_NN_BOOK_SET2 		= LEARNING_RATE_200;
	private static final float LEARNING_RATE_NN_BOOK_SET3 		= LEARNING_RATE_200;
	private static final float LEARNING_RATE_NN_CHESSCOM_SET1 	= LEARNING_RATE_400;
	private static final float LEARNING_RATE_NN_CHESSCOM_SET2 	= LEARNING_RATE_100;
	private static final float LEARNING_RATE_NN_CHESS24COM_SET1 = LEARNING_RATE_10;
	private static final float LEARNING_RATE_NN_LICHESSORG_SET1 = LEARNING_RATE_400;
	
	private static final float LEARNING_RATE_MAX_TOLERANCE 		= 0.333f;
	
	private static final boolean EXIT_ON_BIG_DEVIATION 			= true;
	
	
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
        	
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_universal_extended/",
													"dnet_universal_extended.dnet",
													LEARNING_RATE_NN_UNIVERSAL
								)
			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_1_extended/",
													"dnet_books_set_1_extended.dnet",
													LEARNING_RATE_NN_BOOK_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_2_extended/",
													"dnet_books_set_2_extended.dnet",
													LEARNING_RATE_NN_BOOK_SET2
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_3_extended/",
													"dnet_books_set_3_extended.dnet",
													LEARNING_RATE_NN_BOOK_SET3
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_1_extended/",
													"dnet_chesscom_set_1_extended.dnet",
													LEARNING_RATE_NN_CHESSCOM_SET1
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_2_extended/",
													"dnet_chesscom_set_2_extended.dnet",
													LEARNING_RATE_NN_CHESSCOM_SET2
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chess24com_set_1_extended/",
													"dnet_chess24com_set_1_extended.dnet",
													LEARNING_RATE_NN_CHESS24COM_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
													"dnet_lichessorg_set_1_extended.dnet",
													LEARNING_RATE_NN_LICHESSORG_SET1
								)
					);
        	
        	
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
        imageSet.setGrayscale(true);
        
        imageSet.loadLabels(new File(labelsFile));
        
        try {
        	
            imageSet.loadImages(new File(trainingFile));
            //ImageSet[] imageSets = imageSet.split(0.7, 0.3);
            
            
            LOGGER.info("Training neural network ...");
            
            final ConvolutionalNetwork[] neural_net = new ConvolutionalNetwork[1];
            
            Thread saverThread = new Thread(new Runnable() {
            	
    			@Override
    			public void run() {
    				
    				try {
    					
    					while (!finished) {
    						
    				        Thread.currentThread().sleep(60000);
    				        
    				        if (neural_net[0] != null) {
    				        	
    				        	// Save trained network to file
    				        	FileIO.writeToFile(neural_net[0], OUTPUT_FILE_NAME);
    				        }
    				        
    				        LOGGER.info("Network saved as " + OUTPUT_FILE_NAME);
    				        
    					}
    					
    				} catch(Throwable t) {
    					
    					t.printStackTrace();
    				}
    			}
    		});
            
            saverThread.start();
            
            
            int loop_counter = 0;
            
            int labelsCount = imageSet.getLabelsCount();
            
            final List<Float> train_accuracies = new ArrayList<Float>();
            
            while (train_accuracies.size() == 0) {
            	
            	loop_counter++;
            	
            	LOGGER.error("Starting training for " + OUTPUT_FILE_NAME + " LEARNING_RATE=" + LEARNING_RATE + " try " + loop_counter);
            	
            	neural_net[0] = getNewOrLoadNetwork(new File(OUTPUT_FILE_NAME), labelsCount);
            	
	            // create a trainer and train network
	            final BackpropagationTrainer trainer = neural_net[0].getTrainer();
	            
	            trainer.addListener(new TrainingListener() {
					
					@Override
					public void handleEvent(TrainingEvent event) {
						
						if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
							
							//LOGGER.error("Event EPOCH_FINISHED");
							
							float current_accuracy = event.getSource().getTrainingAccuracy();
							
							train_accuracies.add(current_accuracy);
							
							if (train_accuracies.size() >= 2) {
								
								float prev_accuracy = train_accuracies.get(train_accuracies.size() - 2);
								
								if (current_accuracy == 0 || current_accuracy < prev_accuracy - LEARNING_RATE_MAX_TOLERANCE * prev_accuracy) {
									
									LOGGER.error(OUTPUT_FILE_NAME + " current_accuracy < prev_accuracy - 0.05f * prev_accuracy, current_accuracy=" + current_accuracy + " prev_accuracy=" + prev_accuracy);
									
									LEARNING_RATE *= (1 + LEARNING_RATE_MAX_TOLERANCE);
									
									LOGGER.error("Decreasing LEARNING_RATE to " + LEARNING_RATE);
									
									train_accuracies.clear();
									
									trainer.stop();
									
									if (EXIT_ON_BIG_DEVIATION) {
										
										System.exit(0);
									}
								}
							}
							
						} /*else if (event.getType().equals(TrainingEvent.Type.STOPPED)) {
						
							//LOGGER.error("event STOPPED");
						}*/
					}
				});
	            		
	            trainer.setLearningRate(LEARNING_RATE)
	                    .setMaxError(MAX_ERROR_MEAN_CROSS_ENTROPY)
	                    .setMaxEpochs(10000);
	            
	            
	            trainer.train(imageSet);
	            
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
            }
            
            finished = true;
            
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
	                .addInputLayer(imageWidth, imageHeight, 3)
	                .addConvolutionalLayer(3, 3, 3)
	                //.addMaxPoolingLayer(4, 4, 1)
	                //.addConvolutionalLayer(3)
	                //.addMaxPoolingLayer(4, 1)
	                .addFullyConnectedLayer(4 * labelsCount)
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

