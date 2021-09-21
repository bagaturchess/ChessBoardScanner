package bagaturchess.scanner.machinelearning.learning.impl_deepnetts;


import deepnetts.core.DeepNetts;
import deepnetts.data.ImageSet;
import deepnetts.net.ConvolutionalNetwork;
import deepnetts.util.DeepNettsException;
import deepnetts.eval.ClassifierEvaluator;
import deepnetts.eval.ConfusionMatrix;
import javax.visrec.ml.eval.EvaluationMetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ScannerLearning_ProviderClassifier_Test {

	
	private String TESTS_DIR_NAME 	= "./datasets_deepnetts/dataset_provider_classifier/";
	private String NET_FILE_NAME = "cnn_provider_classifier.dnet";
	
    // download data set and set these paths
	private String labelsFile = TESTS_DIR_NAME + "labels.txt";
	private String trainingFile = TESTS_DIR_NAME + "index.txt";
    
    
	private int imageWidth = 32;
	private int imageHeight = 32;
    
    
    private static final Logger LOGGER = LogManager.getLogger(DeepNetts.class.getName());
    
    
    public void run() throws DeepNettsException, IOException, ClassNotFoundException {
    	
    	LOGGER.info("INPUT TESTS DIR: " + TESTS_DIR_NAME);
    	LOGGER.info("NET FILE: " + NET_FILE_NAME);
    	
        LOGGER.info("Loading images...");
        
        // create a data set from images and labels
        ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
        
        //This is important: with gray scale images, the recognition of chess board squares works better!
        imageSet.setGrayscale(true);
        //imageSet.setInvertImages(true);  
        
        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile));

        
		System.out.println("Loading network ...");
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(NET_FILE_NAME)));
		ConvolutionalNetwork neuralNet = (ConvolutionalNetwork) ois.readObject();
		System.out.println("Network loaded.");
		System.out.println("The network has " + neuralNet.getOutput().length + " outputs");
        
        
        // Test trained network
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        evaluator.evaluate(neuralNet, imageSet);
        
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
        LOGGER.info(cm.toString());
    }
    
    
    public static void main(String[] args) {
    	
        try {
        	
			(new ScannerLearning_ProviderClassifier_Test()).run();
			
		} catch (Throwable t) {
			
			t.printStackTrace();
		}
    }
}

