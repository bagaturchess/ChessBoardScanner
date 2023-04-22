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
import javafx.collections.transformation.SortedList;

import javax.visrec.ml.eval.EvaluationMetrics;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.util.FileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.machinelearning.learning.impl_deepnetts.TrainingUtils.AutoTuningParameters;
import bagaturchess.scanner.machinelearning.learning.impl_deepnetts.model.NetworkModelBuilder;


public class ScannerLearning_Tuning_Edition_Pro13 implements Runnable {
	
	
	private static final float INITIAL_LEARNING_RATE_MAX_TOLERANCE 			= 0.157f;
	
	private static final int INITIAL_EPOCHS_COUNT 							= 2;
	private static final int STEP_EPOCHS_COUNT 								= 2;
	private static final int MAX_EPOCHS_COUNT 								= 1000;

	private static final Map<String, TrainingStatistics> global_stats 		= new Hashtable<String, TrainingStatistics>();

	
    private static final Logger LOGGER 										= LogManager.getLogger(DeepNetts.class.getName());
    
    
	private String INPUT_DIR_NAME;
	private String OUTPUT_FILE_NAME;
	private int count_epochs;
	private float learning_rate_deviation_max_tolerance 					= INITIAL_LEARNING_RATE_MAX_TOLERANCE;
	
    // download data set and set these paths
	private String labelsFile;
	private String trainingFile;
    
	
	private boolean finished 												= false;
	
	
	private ScannerLearning_Tuning_Edition_Pro13(
			String _INPUT_DIR_NAME,
			String _OUTPUT_FILE_NAME
    	) {
		
		this(_INPUT_DIR_NAME, _OUTPUT_FILE_NAME, INITIAL_EPOCHS_COUNT, INITIAL_LEARNING_RATE_MAX_TOLERANCE);
	}
	
	
    private ScannerLearning_Tuning_Edition_Pro13(
    		String _INPUT_DIR_NAME,
    		String _OUTPUT_FILE_NAME,
    		int _count_epochs,
    		float _learning_rate_deviation_max_tolerance
    		) {
    	
    	INPUT_DIR_NAME = _INPUT_DIR_NAME;
    	OUTPUT_FILE_NAME = _OUTPUT_FILE_NAME;
    	count_epochs = _count_epochs;
    	learning_rate_deviation_max_tolerance = _learning_rate_deviation_max_tolerance;
    	
    	labelsFile = INPUT_DIR_NAME + "labels.txt";
    	trainingFile = INPUT_DIR_NAME + "index.txt";
    }
    
    
    public static void main(String[] args) {
    	
    	
        try {
        	
        	
        	List<Runnable> learningTasks = new ArrayList<Runnable>();
        	
        	
        	/*learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_1_extended/",
													"dnet_books_set_1_extended.dnet"
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_2_extended/",
													"dnet_books_set_2_extended.dnet"
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_books_set_3_extended/",
													"dnet_books_set_3_extended.dnet"
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_1_extended/",
													"dnet_chesscom_set_1_extended.dnet"
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_chesscom_set_2_extended/",
													"dnet_chesscom_set_2_extended.dnet"
								)
					);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_chess24com_set_1_extended/",
													"dnet_chess24com_set_1_extended.dnet"
								)
        			);
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_lichessorg_set_1_extended/",
													"dnet_lichessorg_set_1_extended.dnet"
								)
					);
        	*/
        	
        	learningTasks.add(new ScannerLearning_Tuning_Edition_Pro13("./datasets_deepnetts/dataset_universal_extended/",
													"dnet_universal_extended.dnet"
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
        ImageSet imageSet = new ImageSet(TrainingUtils.SQUARE_IMAGE_SIZE, TrainingUtils.SQUARE_IMAGE_SIZE);
        
        //This is important: with gray scale images, the recognition of chess board squares works better!
        //Available only in Pro version of Deep Netts
        if (true) {
        	
        	throw new IllegalStateException("Uncomment the setGrayscale(true) method call below for the pro version.");
        	//imageSet.setGrayscale(true);
        }
        
        imageSet.loadLabels(new File(labelsFile));
        
        try {
        	
            imageSet.loadImages(new File(trainingFile));
            //ImageSet[] imageSets = imageSet.split(0.7, 0.3);
            
            
            LOGGER.info("Auto tuning of convolutional neural network ...");
            
            final ConvolutionalNetwork[] neural_net = new ConvolutionalNetwork[1];
            
            
            int labels_count = imageSet.getLabelsCount();
            
            int loop_counter = 0;
            
            while (count_epochs <= MAX_EPOCHS_COUNT) {
	        
	            for (int layers_count = 1; layers_count <= 3; layers_count++) {
	            	
	             	for (int size_fully_connected_layer = labels_count; size_fully_connected_layer <= labels_count * size_fully_connected_layer; size_fully_connected_layer += labels_count / 2f) {
	        						            
	    	            float LEARNING_RATE_MIN 					= TrainingUtils.LEARNING_RATE_800;
	    	            float LEARNING_RATE_MAX 					= TrainingUtils.LEARNING_RATE_10;
	    	            
	    	            final List<Float> training_accuracies 		= new ArrayList<Float>();
	    	            
	    	            final int[] current_layers_count 			= new int[] {layers_count};
	    	            final float[] current_learning_rate 		= new float[] {LEARNING_RATE_MAX};
	    	            
	    	            while (current_learning_rate[0] >= LEARNING_RATE_MIN) {
	    	            	
	        	            final int[] current_size_fully_connected_layers = new int[] {size_fully_connected_layer};
	        	            
	    	            	neural_net[0] 									= NetworkModelBuilder.build(TrainingUtils.SQUARE_IMAGE_SIZE, labels_count, layers_count, size_fully_connected_layer);
	    	            	
	    	            	// create a trainer and train network
	    		            final BackpropagationTrainer trainer 			= neural_net[0].getTrainer();
	    		            
	    		            final int[] current_count_epochs 				= new int[count_epochs];
	    	            	
	    		            final VarStatistic stats_accuracy 				= new VarStatistic();
	    		            final VarStatistic stats_accuracy_trend 		= new VarStatistic();;
	    		    		
	    	            	training_accuracies.add(0f);
	    	            	
	    	            	loop_counter++;
	    	            	
	    	            	LOGGER.error("Starting training for " + OUTPUT_FILE_NAME + " LEARNING_RATE=" + current_learning_rate[0] + " test " + loop_counter);
							
	    	            	final long start_time = System.currentTimeMillis();
	    	            	
	    	            	trainer.addListener(new TrainingListener() {
	    						
	    						@Override
	    						public void handleEvent(TrainingEvent event) {
	    							
	    							float accuracy = event.getSource().getTrainingAccuracy();
	    							
	    							if (event.getType().equals(TrainingEvent.Type.EPOCH_FINISHED)) {
	    								
	    								String net_parameters_str = "_" + current_layers_count[0]
	    										+ "_" + current_size_fully_connected_layers[0] + "_" + current_learning_rate[0];
	    								
	    								String net_name = OUTPUT_FILE_NAME + net_parameters_str;
	    								
	    								LOGGER.info("TEST EPOCH " + (current_count_epochs[0] + 1) + " FINISHED for " + net_name + " accuracy is " + accuracy);
	    								
	    								float prev_accuracy =  training_accuracies.get(training_accuracies.size() - 1);
	    								
	    								if (accuracy < prev_accuracy - learning_rate_deviation_max_tolerance * prev_accuracy) {
	    									
	    									accuracy = 0;
	    								}
	    								
	    				            	training_accuracies.set(training_accuracies.size() - 1, accuracy);
	    				            	
	        							stats_accuracy.addValue(accuracy);
	        							stats_accuracy_trend.addValue(accuracy - prev_accuracy);
	        							
	        							AutoTuningParameters params = new TrainingUtils.AutoTuningParameters(current_layers_count[0], current_size_fully_connected_layers[0], current_learning_rate[0]);
	        							
	        							System.out.println("count_epochs=" + count_epochs);
	        							System.out.println("current_count_epochs[0]=" + current_count_epochs[0]);
	        							System.out.println("MAX_EPOCH=" + (count_epochs - current_count_epochs[0]));
	        							
	    								global_stats.put(
	    										
	    										net_name,
	    										
	    										new TrainingStatistics(
	    											OUTPUT_FILE_NAME,
	    											net_parameters_str,
		    										accuracy,
		    										System.currentTimeMillis() - start_time,
		    										count_epochs - current_count_epochs[0],
		    										params,
		    										stats_accuracy,
		    										stats_accuracy_trend
		    									)
	    								);
	    								
	    								try {
	    									
											dumpGlobalStatistics();
											
										} catch (FileNotFoundException e) {
											
											e.printStackTrace();
										}
	    								
	    								if (accuracy == 0) {
	    									
	    									current_count_epochs[0] = 1;
	    								}
	    								
	    								current_count_epochs[0]--;
	    								
	    								if (current_count_epochs[0] <= 0) {
	    									
	    									trainer.stop();
	    								}
	    								
	    							} else if (event.getType().equals(TrainingEvent.Type.STOPPED)) {
	    								
	    								//Do nothing
	    							}
	    						}
	    					});
	    		            
	    		            trainer.setLearningRate(current_learning_rate[0])
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
	    		            
	    		            current_learning_rate[0] = 0.5f * current_learning_rate[0];
	    	            }
	            	}
	            }
	            
	            count_epochs += STEP_EPOCHS_COUNT;
            }
            
        } catch (Throwable t) {
        	
        	t.printStackTrace();
        	
        } finally {
        	
        	finished = true;
        }
    }
    
	
	private static final void dumpGlobalStatistics() throws FileNotFoundException {
		
		List<TrainingStatistics> stats_list 				= new ArrayList<TrainingStatistics>(global_stats.values());
		
		Collections.sort(stats_list);
		
		Map<String, List<Object>> cnns_tuning_scores = new HashMap<String, List<Object>>();
		
		for (int i = 0; i < stats_list.size(); i++) {
			
			TrainingStatistics net_stat = stats_list.get(i);
			
			List<Object> scores_lists = new ArrayList<Object>();
			
			scores_lists.add(new HashMap<Integer, Integer>());
			scores_lists.add(new HashMap<Integer, Integer>());
			scores_lists.add(new HashMap<Float, Integer>());
			List<String> text_list = new ArrayList<String>();
			text_list.add("");
			scores_lists.add(text_list);
			
			cnns_tuning_scores.put(net_stat.net_name, scores_lists);
		}
		
		
		String text_delim = "************************************************************************************************************************************************";
		
		System.out.println(text_delim);
		
		for (int i = 0; i < stats_list.size(); i++) {
			
			
			TrainingStatistics net_stat = stats_list.get(i);
			
			String message = net_stat.net_name + net_stat.net_parameters + " maximal achieved accuracy is " + net_stat.accuracy + " in " + net_stat.epochs + " epochs for " + net_stat.time / 1000 + " seconds";
			
			System.out.println(message);
			
			
			List<Object> scores_lists 							= cnns_tuning_scores.get(net_stat.net_name);
			Map<Integer, Integer> scores_layers 				= (Map<Integer, Integer>) scores_lists.get(0);
			Map<Integer, Integer> scores_fully_connected_sizes 	= (Map<Integer, Integer>) scores_lists.get(1);
			Map<Float, Integer> scores_learning_rates 			= (Map<Float, Integer>) scores_lists.get(2);
			List<String> text 									= (List<String>) scores_lists.get(3);
			
			text.set(0, text.get(0) + "\r\n" + message);
			
			if (!scores_layers.containsKey(net_stat.params.count_convolutional_layers)) {
				
				scores_layers.put(net_stat.params.count_convolutional_layers, 0);
			}
			
			scores_layers.replace(net_stat.params.count_convolutional_layers, (i + 1) + scores_layers.get(net_stat.params.count_convolutional_layers));
			
			
			if (!scores_fully_connected_sizes.containsKey(net_stat.params.size_fully_connected_layer)) {
				
				scores_fully_connected_sizes.put(net_stat.params.size_fully_connected_layer, 0);
			}
			
			scores_fully_connected_sizes.replace(net_stat.params.size_fully_connected_layer, (i + 1) + scores_fully_connected_sizes.get(net_stat.params.size_fully_connected_layer));
			
			
			if (!scores_learning_rates.containsKey(net_stat.params.learning_rate)) {
				
				scores_learning_rates.put(net_stat.params.learning_rate, 0);
			}
			
			scores_learning_rates.replace(net_stat.params.learning_rate, (i + 1) + scores_learning_rates.get(net_stat.params.learning_rate));
		}
		
		
		for (String net_name_prefix: cnns_tuning_scores.keySet()) {
			
			List<Object> scores_lists 							= cnns_tuning_scores.get(net_name_prefix);
			Map<Integer, Integer> scores_layers 				= (Map<Integer, Integer>) scores_lists.get(0);
			Map<Integer, Integer> scores_fully_connected_sizes 	= (Map<Integer, Integer>) scores_lists.get(1);
			Map<Float, Integer> scores_learning_rates 			= (Map<Float, Integer>) scores_lists.get(2);
			List<String> text 									= (List<String>) scores_lists.get(3);
			
			
			String message = "\r\n";
			
			for (Integer cur_scores_layers: scores_layers.keySet()) {
				
				message += "\r\nCONVOLUTIONAL LAYERS COUNT: " + cur_scores_layers + " has " + scores_layers.get(cur_scores_layers) + " scores";
			}
			
			for (Integer cur_scores_fully_connected_sizes: scores_fully_connected_sizes.keySet()) {
				
				message += "\r\nFULLY CONNECTED LAYER SIZE: " + cur_scores_fully_connected_sizes + " has " + scores_fully_connected_sizes.get(cur_scores_fully_connected_sizes) + " scores";
			}
			
			for (Float cur_scores_learning_rates: scores_learning_rates.keySet()) {
				
				message += "\r\nLEARNING RATE: " + cur_scores_learning_rates + " has " + scores_learning_rates.get(cur_scores_learning_rates) + " scores";
			}
			
			System.out.println(message);
			
			text.set(0, text.get(0) + message);
		}

		System.out.println(text_delim);
		
		File dir = new File("./tuning");
		
		if (!dir.exists()) {
			
			dir.mkdir();
		}
		
		for (String net_name_prefix: cnns_tuning_scores.keySet()) {
			
			List<Object> scores_lists 							= cnns_tuning_scores.get(net_name_prefix);
			List<String> text 									= (List<String>) scores_lists.get(3);
			
			PrintWriter out_file = new PrintWriter("./tuning/measure_params_" + net_name_prefix + ".txt");
			out_file.println(text.get(0));
			out_file.close();
		}
	}
	
	
	private static final class TrainingStatistics implements Comparable<TrainingStatistics> {
		
		
		String net_name;
		String net_parameters;
		float accuracy;
		long time;
		int epochs;
		AutoTuningParameters params;
		VarStatistic stats_accuracy;
		VarStatistic stats_accuracy_trend;
		
		
		TrainingStatistics(String _net_name, String _net_parameters, float _accuracy, long _time, int _epochs,
				AutoTuningParameters _params,
				VarStatistic _stats_accuracy, VarStatistic _stats_accuracy_trend) {
			
			net_name = _net_name;
			net_parameters = _net_parameters;
			accuracy = _accuracy;
			time =_time;
			epochs = _epochs;
			params = _params;
			stats_accuracy = _stats_accuracy;
			stats_accuracy_trend = _stats_accuracy_trend;
		}
		
		
		@Override
		public int compareTo(TrainingStatistics other) {
			
			float delta_accuracy = 1000f * (this.accuracy - other.accuracy);
			
			if (delta_accuracy != 0) {
				
				return (int) delta_accuracy;
			}
			
			float delta_time = this.time - other.time;
			
			return (int) (-1 * 10000 * delta_time);
		}
		
		
		@Override
		public String toString() {
			
			return "TrainingStatistics: [" + net_name + net_parameters + "		" + accuracy + "		" + time + "		" + epochs
					+ "		" + params.count_convolutional_layers + "		" + params.size_fully_connected_layer + "		" + params.learning_rate
					+ "		" + stats_accuracy.getDisperse() + "		" + stats_accuracy_trend.getChaos() + "]";
		}
	}
}

