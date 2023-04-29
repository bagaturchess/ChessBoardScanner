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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
    
	private static final int MIN_EPOCHS_FOR_DIFF 						= 10;
	
    private static final Map<String, List<Float>> global_accuracies 	= new Hashtable<String, List<Float>>();
	private static final Map<String, Integer> global_epochs 			= new Hashtable<String, Integer>();
	private static final Map<String, Long> global_times 				= new Hashtable<String, Long>();
	private static final Map<String, Float> global_learning_rates		= new Hashtable<String, Float>();
	
	
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
            	//imageSet.setGrayscale(true);
            }
            
            imageSet.loadLabels(new File(labelsFile));
            
            imageSet.loadImages(new File(trainingFile));
            //ImageSet[] imageSets = imageSet.split(0.7, 0.3);
            
            
            LOGGER.info("Training neural network ...");
            
            
            float LEARNING_RATE_MIN = TrainingUtils.MIN_LEARNING_RATE;
            
            final float[] final_current_learning_rate = new float[] {training_params.learning_rate};
            
            int iteration_counter = 0;
            
            final boolean[] training_completed_succesfully = new boolean[1];
            
            while (!training_completed_succesfully[0] && final_current_learning_rate[0] >= LEARNING_RATE_MIN) {
            	
            	iteration_counter++;
            	
                final ConvolutionalNetwork[] neural_net = new ConvolutionalNetwork[1];
                
                final Integer[] epochs_count = new Integer[1];
                final long start_time = System.currentTimeMillis();
                
                int labels_count = imageSet.getLabelsCount();
                
                LOGGER.error("Starting training for " + OUTPUT_FILE_NAME
                		+ " with parameters " + training_params
                		+ ", try " + iteration_counter + ", current learning rate " + final_current_learning_rate[0]);
                
                neural_net[0] = NetworkModelBuilder.build(TrainingUtils.SQUARE_IMAGE_SIZE, labels_count, training_params.count_convolutional_layers, training_params.convolution_filter_size, training_params.size_fully_connected_layer);
                
                // create a trainer and train network
                final BackpropagationTrainer trainer = neural_net[0].getTrainer();
                
                trainer.addListener(new TrainingListener() {
    				
    				@Override
    				public void handleEvent(TrainingEvent event) {
    					
    					float accuracy = event.getSource().getTrainingAccuracy();
    					
    					if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
    						
    						if (epochs_count[0] == null) {
    							
    							epochs_count[0] = 0;
    						}
    						
    						epochs_count[0]++;
    						
    						List<Float> accuracies = global_accuracies.get(OUTPUT_FILE_NAME);
    						
    						if (accuracies == null) {
    							
    							accuracies = new ArrayList<Float>();
    						}
    						
    						accuracies.add(accuracy);
    						
    						global_accuracies.put(OUTPUT_FILE_NAME, accuracies);
    						global_epochs.put(OUTPUT_FILE_NAME, epochs_count[0]);
    						global_times.put(OUTPUT_FILE_NAME, System.currentTimeMillis() - start_time);
    						global_learning_rates.put(OUTPUT_FILE_NAME, final_current_learning_rate[0]);
    						
    			        	try {
    			        		
    							dumpGlobalAccuracies();
    							
    							LOGGER.info("EPOCH_FINISHED for " + OUTPUT_FILE_NAME);
    							
    							FileIO.writeToFile(neural_net[0], OUTPUT_FILE_NAME);
    							
    					        LOGGER.info("Network saved as " + OUTPUT_FILE_NAME);
    					        
    						} catch (IOException e) {
    							
    							e.printStackTrace();
    						}
    			        	
    						
    			        	int MIN_EPOCHS = MIN_EPOCHS_FOR_DIFF;
    			        	
    			        	if (accuracies.size() >= MIN_EPOCHS
    			        			&& accuracy < 0.5f //Not at the end of training.
    			        		) {
    							
    			        		boolean all_are_equal = true;
    			        		
    			        		float prev = -1;
    			        		
    			        		for (int i = accuracies.size() - 1; i >=0; i--) {
    			        			
    			        			float cur = accuracies.get(i);
    			        			
    			        			if (prev != -1) {
    			        				
        			        			if (prev != cur) {
        			        				
        			        				all_are_equal = false;
        			        				
        			        				break;
        			        			}
    			        			}

    			        			
    			        			prev = cur;
    			        		}
    			        		
    			        		LOGGER.info(OUTPUT_FILE_NAME + " accuracies all_are_equal=" + all_are_equal);
    			        		
    							if (all_are_equal) {
    								
    								LOGGER.info("Accuracy is not changing " + MIN_EPOCHS + " epochs! It is equal to " + accuracy
    										+ ". Now, setting it to 0 for " + OUTPUT_FILE_NAME
    										+ " in order to stop the training with the current learning rate.");
    								
    								accuracy = 0;
    								
    								//accuracies.set(accuracies.size() - 1, 0f);
    							}
    						}
    						
							if (accuracy == 0) {
								
								final_current_learning_rate[0] -= training_params.learning_rate_decrease_percent * final_current_learning_rate[0];
								
								//Clear global maps for this net
								global_accuracies.put(OUTPUT_FILE_NAME, new ArrayList<Float>());
	    						global_epochs.put(OUTPUT_FILE_NAME, 0);
	    						global_times.put(OUTPUT_FILE_NAME, 0L);
	    						//global_learning_rates.put(OUTPUT_FILE_NAME, 0f);
	    						
								trainer.stop();
							}
							
							if (accuracy == 1f) {
								
								training_completed_succesfully[0] = true;
							}
    					}
    				}
    			});
                
                trainer.setLearningRate(final_current_learning_rate[0])
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
            }
            
        } catch (Throwable t) {
        	
        	t.printStackTrace();
        	
        } finally {
        	
        	finished = true;
        }
    }
    
    
	private static final void dumpGlobalAccuracies() throws FileNotFoundException {
		
		String message = "";
		
		for (String net_name: global_accuracies.keySet()) {
			
			List<Float> accuracies 		= global_accuracies.get(net_name);
			int epochs 					= global_epochs.get(net_name);
			long time 					= global_times.get(net_name);
			float current_learning_rate = global_learning_rates.get(net_name);
			
			message += "\r\n";
			message += net_name + "> accuracy=" + (accuracies.size() == 0 ? 0 : accuracies.get(accuracies.size() - 1))
						+ " epochs=" + epochs + " time=" + time / 1000 + "sec, LR=" + current_learning_rate + " All_Accuracies=" + accuracies;
		}
		
		System.out.println(message);
		
		
		File dir = new File("./training");
		
		if (!dir.exists()) {
			
			dir.mkdir();
		}
		
		PrintWriter out_file = new PrintWriter("./training/progress.txt");
		out_file.println(message);
		out_file.close();
	}
}

