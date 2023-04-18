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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ScannerLearning_Edition_Pro13 implements Runnable {
	
	
	private static final long SAVE_NET_FILE_INTERVAL 					= 3 * 60 * 1000;
	
	private static final float MAX_ERROR_MEAN_CROSS_ENTROPY 			= 0.0001f;
	
	private static final float MAX_ERROR_MEAN_SQUARED_ERROR 			= MAX_ERROR_MEAN_CROSS_ENTROPY / 1000f;
	
	private static final float LEARNING_RATE_1 							= 1f;
	private static final float LEARNING_RATE_2 							= 0.5f;
	private static final float LEARNING_RATE_4 							= 0.25f;
	private static final float LEARNING_RATE_10 						= 0.1f;
	private static final float LEARNING_RATE_20 						= 0.05f;
	private static final float LEARNING_RATE_50 						= 0.02f;
	private static final float LEARNING_RATE_100 						= 0.01f;
	private static final float LEARNING_RATE_200 						= 0.005f;
	private static final float LEARNING_RATE_400 						= 0.0025f;
	private static final float LEARNING_RATE_800 						= 0.00125f;
	
	private static final float LEARNING_RATE_1K 						= 0.001f;
	private static final float LEARNING_RATE_2K 						= 0.0005f;
	private static final float LEARNING_RATE_4K 						= 0.00025f;
	private static final float LEARNING_RATE_8K 						= 0.000125f;
	private static final float LEARNING_RATE_10K 						= 0.000125f;
	private static final float LEARNING_RATE_16K 						= 0.0000625f;

	private static final float LEARNING_RATE_INIT_NN_BOOK_SET1 			= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_BOOK_SET2 			= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_BOOK_SET3 			= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_CHESSCOM_SET1 		= LEARNING_RATE_1;
	private static final float LEARNING_RATE_INIT_NN_CHESSCOM_SET2 		= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_CHESS24COM_SET1 	= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_LICHESSORG_SET1 	= LEARNING_RATE_10;
	private static final float LEARNING_RATE_INIT_NN_UNIVERSAL 			= LEARNING_RATE_1;
	
	private static final float INITIAL_LEARNING_RATE_MAX_TOLERANCE 		= 0.157f;
	
	private static final boolean AUTO_LEARNING_RATE 					= true;
	
	private static final int INITIAL_AUTO_LEARNING_RATE_EPOCHS_COUNT 	= 7;

	private static final Map<String, Float> global_accuracies 			= new Hashtable<String, Float>();
	private static final Map<String, Integer> global_epochs 			= new Hashtable<String, Integer>();
	private static final Map<String, Long> global_times 				= new Hashtable<String, Long>();
	
    private static final Logger LOGGER 									= LogManager.getLogger(DeepNetts.class.getName());
    
    
	private String INPUT_DIR_NAME;
	private String OUTPUT_FILE_NAME;
	private float LEARNING_RATE;
	
	
    // download data set and set these paths
	private String labelsFile;
	private String trainingFile;
    
    
	private int imageWidth 												= 32;
	private int imageHeight 											= 32;
    
	
	private boolean finished 											= false;
	
	private int AUTO_LEARNING_RATE_EPOCHS_COUNT 						= INITIAL_AUTO_LEARNING_RATE_EPOCHS_COUNT;
	
	private float LEARNING_RATE_MAX_TOLERANCE 							= INITIAL_LEARNING_RATE_MAX_TOLERANCE;
	
	
	private ScannerLearning_Edition_Pro13(String _INPUT_DIR_NAME, String _OUTPUT_FILE_NAME, float _LEARNING_RATE) {
		
		this(_INPUT_DIR_NAME, _OUTPUT_FILE_NAME, _LEARNING_RATE, INITIAL_LEARNING_RATE_MAX_TOLERANCE);
	}
	
	
    private ScannerLearning_Edition_Pro13(String _INPUT_DIR_NAME, String _OUTPUT_FILE_NAME, float _LEARNING_RATE, float _LEARNING_RATE_MAX_TOLERANCE) {
    	
    	INPUT_DIR_NAME = _INPUT_DIR_NAME;
    	OUTPUT_FILE_NAME = _OUTPUT_FILE_NAME;
    	LEARNING_RATE = _LEARNING_RATE;
    	LEARNING_RATE_MAX_TOLERANCE = _LEARNING_RATE_MAX_TOLERANCE;
    	
    	labelsFile = INPUT_DIR_NAME + "labels.txt";
    	trainingFile = INPUT_DIR_NAME + "index.txt";
    }
    
    
    public static void main(String[] args) {
    	
    	
        try {
        	
        	
        	List<Runnable> learningTasks = new ArrayList<Runnable>();
        	
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_1_extended/",
													"dnet_books_set_1_extended.dnet",
													LEARNING_RATE_INIT_NN_BOOK_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_2_extended/",
													"dnet_books_set_2_extended.dnet",
													LEARNING_RATE_INIT_NN_BOOK_SET2
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_3_extended/",
													"dnet_books_set_3_extended.dnet",
													LEARNING_RATE_INIT_NN_BOOK_SET3
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_1_extended/",
													"dnet_chesscom_set_1_extended.dnet",
													LEARNING_RATE_INIT_NN_CHESSCOM_SET1
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_2_extended/",
													"dnet_chesscom_set_2_extended.dnet",
													LEARNING_RATE_INIT_NN_CHESSCOM_SET2
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_chess24com_set_1_extended/",
													"dnet_chess24com_set_1_extended.dnet",
													LEARNING_RATE_INIT_NN_CHESS24COM_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
													"dnet_lichessorg_set_1_extended.dnet",
													LEARNING_RATE_INIT_NN_LICHESSORG_SET1
								)
					);

        	learningTasks.add(new ScannerLearning_Edition_Pro13("./datasets_deepnetts/dataset_universal_extended/",
													"dnet_universal_extended.dnet",
													LEARNING_RATE_INIT_NN_UNIVERSAL,
													0.49f
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
        //Available only in Pro version of Deep Netts
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
    						
    				        Thread.sleep(SAVE_NET_FILE_INTERVAL);
    				        
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
            
            
            int labelsCount = imageSet.getLabelsCount();
            
            if (AUTO_LEARNING_RATE) {
            
	            float LEARNING_RATE_MIN = LEARNING_RATE_10K;
	            float LEARNING_RATE_MAX = LEARNING_RATE;
	            
	            int loop_counter = 0;
	            
	            final List<Float> training_learning_rates = new ArrayList<Float>();
	            final List<Float> training_accuracies = new ArrayList<Float>();
	            
	            while (LEARNING_RATE >= LEARNING_RATE_MIN) {
	            	
	            	training_learning_rates.add(0f);
	            	
	            	training_accuracies.add(0f);
	            	
	            	loop_counter++;
	            	
	            	LOGGER.error("Starting training for " + OUTPUT_FILE_NAME + " LEARNING_RATE=" + LEARNING_RATE + " test " + loop_counter);
	            	
	            	neural_net[0] = getNewOrLoadNetwork(new File(OUTPUT_FILE_NAME), labelsCount);
	            	
	            	// create a trainer and train network
		            final BackpropagationTrainer trainer = neural_net[0].getTrainer();
		            
	            	trainer.addListener(new TrainingListener() {
						
						@Override
						public void handleEvent(TrainingEvent event) {
							
							float accuracy = event.getSource().getTrainingAccuracy();
							
							if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
								
								LOGGER.info("TEST EPOCH_FINISHED for " + OUTPUT_FILE_NAME + " and learning rate " + LEARNING_RATE + " accuracy is " + accuracy);
								
								AUTO_LEARNING_RATE_EPOCHS_COUNT--;
								
								float prev_accuracy =  training_accuracies.get(training_accuracies.size() - 1);
								
								if (accuracy < prev_accuracy - LEARNING_RATE_MAX_TOLERANCE * prev_accuracy) {
									
									accuracy = 0;
								}
								
				            	training_learning_rates.set(training_learning_rates.size() - 1, LEARNING_RATE);
				            	
				            	training_accuracies.set(training_accuracies.size() - 1, accuracy);
				            	
				            	
								if (AUTO_LEARNING_RATE_EPOCHS_COUNT == 0
										|| accuracy == 0
									) {
									
									trainer.stop();
								}
								
							} else if (event.getType().equals(TrainingEvent.Type.STOPPED)) {
								
								AUTO_LEARNING_RATE_EPOCHS_COUNT = INITIAL_AUTO_LEARNING_RATE_EPOCHS_COUNT;
								
								LEARNING_RATE *= 0.5f;
								
								LOGGER.error(OUTPUT_FILE_NAME + " decreasing LEARNING_RATE to " + LEARNING_RATE);
							}
						}
					});
		            		
		            trainer.setLearningRate(LEARNING_RATE)
		                    .setMaxError(MAX_ERROR_MEAN_CROSS_ENTROPY)
		                    .setMaxEpochs(10000);
		            
		            
		            while (true) {
		            	
			            try {
			            	
			            	trainer.train(imageSet);
			            	
			            	break;
			            	
			            } catch (java.util.concurrent.RejectedExecutionException ree) {
			            	
			            	ree.printStackTrace();
			            	
				            Thread.sleep(1000);
			            }
		            }
	            }
	            
	            float best_learning_rate 	= Float.MIN_VALUE;
	            float best_accuracy 		= Float.MIN_VALUE;
	            
	            for (int i = 0; i < training_learning_rates.size(); i++) {
	            	
	            	float learning_rate = training_learning_rates.get(i);
	            	float accuracy 		= training_accuracies.get(i);
	            	
	            	if (accuracy > best_accuracy) {
	            		
	            		best_accuracy = accuracy;
	            		best_learning_rate = learning_rate;
	            	}
	            }
	            
	            LEARNING_RATE = best_learning_rate;
            }
            
            
            LOGGER.error("Starting training for " + OUTPUT_FILE_NAME + " with best LEARNING_RATE=" + LEARNING_RATE);
            
            neural_net[0] = getNewOrLoadNetwork(new File(OUTPUT_FILE_NAME), labelsCount);
        	
            final Integer[] epochs_count = new Integer[1];
            final long start_time = System.currentTimeMillis();
            
            // create a trainer and train network
            final BackpropagationTrainer trainer = neural_net[0].getTrainer();
            
            trainer.addListener(new TrainingListener() {
				
				@Override
				public void handleEvent(TrainingEvent event) {
					
					if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
						
						if (epochs_count[0] == null) {
							epochs_count[0] = new Integer(0);
						}
						epochs_count[0]++;
						
						global_accuracies.put(OUTPUT_FILE_NAME, event.getSource().getTrainingAccuracy());
						global_epochs.put(OUTPUT_FILE_NAME, epochs_count[0]);
						global_times.put(OUTPUT_FILE_NAME, System.currentTimeMillis() - start_time);
						
						dumpGlobalAccuracies();
						
						LOGGER.info("EPOCH_FINISHED for " + OUTPUT_FILE_NAME);
					}
				}
			});
            		
            trainer.setLearningRate(LEARNING_RATE)
                    .setMaxError(MAX_ERROR_MEAN_CROSS_ENTROPY)
                    .setMaxEpochs(10000);
            
            
            while (true) {
            	
	            try {
	            	
	            	trainer.train(imageSet);
	            	
	            	break;
	            	
	            } catch (java.util.concurrent.RejectedExecutionException ree) {
	            	
	            	ree.printStackTrace();
	            	
		            Thread.sleep(1000);
	            }
            }

	            
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
        	
        } finally {
        	
        	finished = true;
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
	                .addConvolutionalLayer(3, 2, 2)
	                .addMaxPoolingLayer(2, 2)
	                .addConvolutionalLayer(3, 2, 2)
	                .addMaxPoolingLayer(2, 2)
	                //TODO: test with 3 Convolutional Layers
	                .addConvolutionalLayer(3, 2, 2)
	                .addMaxPoolingLayer(2, 2)
	                .addFullyConnectedLayer(8 * labelsCount)
	                .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
	                .hiddenActivationFunction(ActivationType.TANH)
	                .lossFunction(LossType.CROSS_ENTROPY)
	                .randomSeed(777)
	                .build();
	        
			
	        System.out.println("Network created.");
		}
		
		return neuralNet;
	}
	
	
	private static final void dumpGlobalAccuracies() {
		
		for (String net_name: global_accuracies.keySet()) {
			
			float accuracy = global_accuracies.get(net_name);
			int epochs = global_epochs.get(net_name);
			long time = global_times.get(net_name);
			
			System.out.println(net_name + " accuracy is " + accuracy + " epochs are " + epochs + " training time is " + time / 1000 + " seconds");
		}
		
	}
	
	
	private static final class TrainingListener_AutoLearningRate implements TrainingListener {

		@Override
		public void handleEvent(TrainingEvent event) {
			

		}
		
	}
}

