package bagaturchess.scanner.cnn.impl_dl4j;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.datavec.image.loader.ImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;


public class CalcNetworkAll {
	
	
	public static void main(String[] args) throws Exception {
		
		int height = 32;
		int width = 32;
		int channels = 1;
		
		File rootDir 			= new File("./CNNinput/lichessorg1");
		File locationToLoad 	= new File("./CNNinput/lichessorg1/trained.lichessorg1.bin");
		
		File testData = new File(rootDir, "testing");
		
		MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(locationToLoad);
		ImageLoader loader = new ImageLoader(height, width, channels);
		//DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
		
		File[] labels = testData.listFiles();
		for (int i = 0; i < labels.length; i++) {
			File label = labels[i];
			File[] images = label.listFiles();
			for (int j = 0; j < images.length; j++) {
				File imageFile = images[j];
				BufferedImage image = ImageIO.read(imageFile);
				
		        INDArray input = loader.asMatrix(image).reshape(1, channels, height, width);
		        //scaler.fit(new DataSet(input, null));
		        //scaler.transform(input);
		        
		        INDArray output = network.output(input, false);
		        System.out.println(label.getName() + " => " + output);
			}
		}
	}
}
