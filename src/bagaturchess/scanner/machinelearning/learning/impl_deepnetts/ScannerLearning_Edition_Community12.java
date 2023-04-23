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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bagaturchess.scanner.machinelearning.learning.impl_deepnetts.TrainingUtils.AutoTuningParameters;
import bagaturchess.scanner.machinelearning.learning.impl_deepnetts.model.NetworkModelBuilder;


public class ScannerLearning_Edition_Community12 implements Runnable {
	
	
    private static final Logger LOGGER 	= LogManager.getLogger(DeepNetts.class.getName());
    
	
    private static final Map<String, Float> global_accuracies 			= new Hashtable<String, Float>();
	private static final Map<String, Integer> global_epochs 			= new Hashtable<String, Integer>();
	private static final Map<String, Long> global_times 				= new Hashtable<String, Long>();
	
	
	private String INPUT_DIR_NAME;
	private String OUTPUT_FILE_NAME;
	private AutoTuningParameters training_params;
	
	
    // download data set and set these paths
	private String labelsFile;
	private String trainingFile;
    
	
	private boolean finished 			= false;
	
	
	private ScannerLearning_Edition_Community12(String _INPUT_DIR_NAME, String _OUTPUT_FILE_NAME, AutoTuningParameters _training_params) {
		
		this(_INPUT_DIR_NAME, _OUTPUT_FILE_NAME, _training_params, 0);
	}
	
	
    private ScannerLearning_Edition_Community12(String _INPUT_DIR_NAME, String _OUTPUT_FILE_NAME, AutoTuningParameters _training_params, float _LEARNING_RATE_MAX_TOLERANCE) {
    	
    	INPUT_DIR_NAME = _INPUT_DIR_NAME;
    	OUTPUT_FILE_NAME = _OUTPUT_FILE_NAME;
    	training_params = _training_params;
    	
    	labelsFile = INPUT_DIR_NAME + "labels.txt";
    	trainingFile = INPUT_DIR_NAME + "index.txt";
    }
    
    
    public static void main(String[] args) {
    	
    	
        try {
        	
        	
        	List<Runnable> learningTasks = new ArrayList<Runnable>();
        	
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_books_set_1_extended/",
													"dnet_books_set_1_extended.dnet",
													TrainingUtils.CNN_BOOK_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_books_set_2_extended/",
													"dnet_books_set_2_extended.dnet",
													TrainingUtils.CNN_BOOK_SET2
								)
					);
        	
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_books_set_3_extended/",
													"dnet_books_set_3_extended.dnet",
													TrainingUtils.CNN_BOOK_SET3
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_chesscom_set_1_extended/",
													"dnet_chesscom_set_1_extended.dnet",
													TrainingUtils.CNN_CHESSCOM_SET1
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_chesscom_set_2_extended/",
													"dnet_chesscom_set_2_extended.dnet",
													TrainingUtils.CNN_CHESSCOM_SET2
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_chess24com_set_1_extended/",
													"dnet_chess24com_set_1_extended.dnet",
													TrainingUtils.CNN_CHESS24COM_SET1
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
													"dnet_lichessorg_set_1_extended.dnet",
													TrainingUtils.CNN_LICHESSORG_SET1
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Edition_Community12("./datasets_deepnetts/dataset_universal_extended/",
													"dnet_universal_extended.dnet",
													TrainingUtils.CNN_UNIVERSAL
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
        
        try {
        	
            // create a data set from images and labels
            ImageSet imageSet = new ImageSet(TrainingUtils.SQUARE_IMAGE_SIZE, TrainingUtils.SQUARE_IMAGE_SIZE);
            
            //This is important: with gray scale images, the recognition of chess board squares works better!
            //Available only in Pro version of Deep Netts
            if (true) {
            	
            	//throw new IllegalStateException("Uncomment the setGrayscale(true) method call below for the pro version.");
            	imageSet.setGrayscale(true);
            }
            
            imageSet.loadLabels(new File(labelsFile));
            
            imageSet.loadImages(new File(trainingFile));
            //ImageSet[] imageSets = imageSet.split(0.7, 0.3);
            
            
            LOGGER.info("Training neural network ...");
            
            final ConvolutionalNetwork[] neural_net = new ConvolutionalNetwork[1];
            
            final Integer[] epochs_count = new Integer[1];
            final long start_time = System.currentTimeMillis();
            
            int labels_count = imageSet.getLabelsCount();
            
            LOGGER.error("Starting training for " + OUTPUT_FILE_NAME + " with best parameters=" + training_params);
            
            neural_net[0] = NetworkModelBuilder.build(TrainingUtils.SQUARE_IMAGE_SIZE, labels_count, training_params.count_convolutional_layers, training_params.convolution_filter_size, training_params.size_fully_connected_layer);
            
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
				        	
			        	// Save trained network to file
			        	try {
			        		
							FileIO.writeToFile(neural_net[0], OUTPUT_FILE_NAME);
							
					        LOGGER.info("Network saved as " + OUTPUT_FILE_NAME);
					        
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					}
				}
			});
            		
            trainer.setLearningRate(training_params.learning_rate)
                    .setMaxError(TrainingUtils.MAX_ERROR_MEAN_CROSS_ENTROPY)
                    .setMaxEpochs(TrainingUtils.MAX_EPOCHS);
            
            
            while (true) {
            	
	            try {
	            	
	            	trainer.train(imageSet);
	            	
	            	break;
	            	
	            } catch (java.util.concurrent.RejectedExecutionException ree) {
	            	
	            	System.out.println("RejectedExecutionException - will retry.");
	            	//ree.printStackTrace();
	            	
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
    
    
	private static final void dumpGlobalAccuracies() {
		
		for (String net_name: global_accuracies.keySet()) {
			
			float accuracy = global_accuracies.get(net_name);
			int epochs = global_epochs.get(net_name);
			long time = global_times.get(net_name);
			
			System.out.println(net_name + " accuracy is " + accuracy + " epochs are " + epochs + " training time is " + time / 1000 + " seconds");
		}
		
	}
}

