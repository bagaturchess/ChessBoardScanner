package bagaturchess.scanner.machinelearning.dataset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatasetGenerator_ChessPiecesProviderClassifier {
	public static void main(String[] args) {
		
		try {
			
			
			String[] inputDirs = new String[] {
					"./datasets_deepnetts/dataset_books_set_1/",
					"./datasets_deepnetts/dataset_chess24com_set_1/",
					"./datasets_deepnetts/dataset_chesscom_set_1/",
					"./datasets_deepnetts/dataset_lichessorg_set_1/",
				};
			
			
			String[] outputDirs = new String[] {
					"./datasets_deepnetts/dataset_provider_classifier/0_books_set_1/",
					"./datasets_deepnetts/dataset_provider_classifier/1_chess24com/",
					"./datasets_deepnetts/dataset_provider_classifier/2_chesscom/",
					"./datasets_deepnetts/dataset_provider_classifier/3_lichessorg/",
				};
			
			
	        for (int i = 0; i < inputDirs.length; i++) {
	        	copy(new File(inputDirs[i]), new File(outputDirs[i]));
	        }
	        
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void copy(File src, File dest) throws IOException {
		
		if (!src.isDirectory()) {
			throw new IllegalStateException();
		}
		
		File[] labels = src.listFiles();
		
		for (File label : labels) {
			
			if (label.isDirectory()) {
				
				File[] images = label.listFiles();
				
				for (File image : images) {
			        InputStream in = new FileInputStream(image);
			        OutputStream out = new FileOutputStream(dest + "/" + image.getName()); 

			        byte[] buffer = new byte[1024];

			        int length;
			        while ((length = in.read(buffer)) > 0){
			            out.write(buffer, 0, length);
			        }

			        in.close();
			        out.close();
				}
			}
        }
	}
}
