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
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ScannerLearning {

	
	private String INPUT_DIR_NAME 	= "./datasets_deepnetts/dataset_lichessorg_set_1/";
	private String OUTPUT_FILE_NAME = "cnn_deepnetts_lichessorg_set_1.dnet";
	
	
    // download data set and set these paths
	private String labelsFile = INPUT_DIR_NAME + "labels.txt";
	private String trainingFile = INPUT_DIR_NAME + "index.txt";
    
    
	private int imageWidth = 32;
	private int imageHeight = 32;
    
    
    private static final Logger LOGGER = LogManager.getLogger(DeepNetts.class.getName());
    
    
    private static boolean finished = false;
    
    
    public void run() throws DeepNettsException, IOException {
    	
    	LOGGER.info("INPUT DIR: " + INPUT_DIR_NAME);
    	LOGGER.info("OUTPUT FILE: " + OUTPUT_FILE_NAME);
    	
        LOGGER.info("Training convolutional network");
        LOGGER.info("Loading images...");
        
        // create a data set from images and labels
        ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
        
        //This is important: with gray scale images, the recognition of chess board squares works better!
        imageSet.setGrayscale(true);
        //imageSet.setInvertImages(true);  
        
        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile));

        int labelsCount = imageSet.getLabelsCount();
        
        //ImageSet[] imageSets = imageSet.split(0.7, 0.3);

        LOGGER.info("Creating neural network ...");

        // create convolutional neural network architecture
        /*ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
                .addInputLayer(imageWidth, imageHeight)
                .addConvolutionalLayer(3, 3)
                .addMaxPoolingLayer(2, 2)         
                .addConvolutionalLayer(6, 3)
                .addMaxPoolingLayer(2, 2)  
                .addConvolutionalLayer(12, 3)
                .addMaxPoolingLayer(2, 2)                  
                .addFullyConnectedLayer(48)
                .addFullyConnectedLayer(48)
                .addFullyConnectedLayer(48)
                .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.TANH)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();*/

        final ConvolutionalNetwork neuralNet =  ConvolutionalNetwork.builder()
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
		
        LOGGER.info("Training neural network ...");

        
        Thread saverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!finished) {
				        Thread.currentThread().sleep(10000);
				        // Save trained network to file
				        FileIO.writeToFile(neuralNet, OUTPUT_FILE_NAME);
					}
				} catch(Throwable t) {
					t.printStackTrace();
				}
			}
		});
        saverThread.start();
        
        
        // create a trainer and train network
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        
        trainer.setLearningRate(0.001f)
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
    }
    
    
    public static void main(String[] args) {
    	
        try {
        	
			(new ScannerLearning()).run();
			
		} catch (Throwable t) {
			
			t.printStackTrace();
		}
    }
}

