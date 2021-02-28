package bagaturchess.scanner.cnn.impl_dl4j;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import bagaturchess.scanner.cnn.impl_dl4j.learning.DataSetIteratorImpl;


public class BuildNetwork1 {
	
	
	public static void main(String[] args) throws Exception {
        
		File rootDir = new File("./CNNinput/lichessorg1");
		
		File locationToSave = new File(rootDir, "trained.lichessorg1.bin");
		
		int height = 32;
		int width = 32;
		int channels = 1;
		//int rngseed = 777;
		int numEpochs = 100;
		
		File trainData = new File(rootDir, "training");
		
		
		//Create network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                //.seed(rngseed)
                //.updater(new Adam.Builder().learningRate(0.00001).build())
        		//.updater(new Nesterovs(0.00000000000000001, 0))
        		//.updater(new RmsProp(0.00000000000000001))
                .activation(Activation.TANH)
                //.activation(Activation.SIGMOID)
                //.weightInit(WeightInit.XAVIER)
                .list()
                .layer(new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[]{0, 0}).name("cnn1").nIn(1).nOut(64).biasInit(0).build())
                .layer(new SubsamplingLayer.Builder(new int[] {2, 2}, new int[] {2, 2}).name("maxpool1").build())
                .layer(new ConvolutionLayer.Builder(new int[] {5, 5}, new int[] {1, 1}, new int[]{0, 0}).name("cnn2").nIn(64).nOut(16).biasInit(0).build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(13)
                        .activation(Activation.SOFTMAX)
                        .build())
                .setInputType(InputType.convolutional(height, width, channels))
                .build();
        
        
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
		
        network.init();
        network.setListeners(new ScoreIterationListener(100));
        
        
        //Generate data set
        DataSetIteratorImpl dataset = new DataSetIteratorImpl();
        
        ImageLoader loader = new ImageLoader(height, width, channels);
		File[] labels = trainData.listFiles();
		for (int i = 0; i < labels.length; i++) {
			File label = labels[i];
			File[] images = label.listFiles();
			for (int j = 0; j < images.length; j++) {
				File imageFile = images[j];
				BufferedImage image = ImageIO.read(imageFile);
				
		        INDArray input = loader.asMatrix(image).reshape(1, channels, height, width);
		        
		        double[][] outputArray = new double[1][13];
		        outputArray[0][Integer.parseInt(label.getName())] = 1d;
		        INDArray output = Nd4j.create(outputArray);
		        
		        dataset.addEntry(new DataSet(input, output));
			}
		}
		
		
		//Scale data
		//DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        //scaler.fit(dataset);
        //dataset.setPreProcessor(scaler);
        
        
        //Train network
		for (int e = 0; e < numEpochs; e++) {
			dataset.reset();
			network.fit(dataset);
		}
		
		
		//Save network
		boolean saveUpdater = true;
		ModelSerializer.writeModel(network, locationToSave, saveUpdater);
	}
}