package bagaturchess.scanner.machinelearning.dataset;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.common.BoardProperties;


public class DatasetGenerator_ByBoardImage {
	
	
	public static void main(String[] args) {
		
		try {
			
			String[] inputFiles = new String[] {
					/*"./res/cnn/chess.com/set1/input1.png",
					"./res/cnn/chess.com/set1/input2.png",
					"./res/cnn/chess.com/set1/input3.png",
					"./res/cnn/chess.com/set1/input4.png",
					"./res/cnn/chess.com/set1/input5.png",
					"./res/cnn/chess.com/set1/input6.png",
					"./res/cnn/chess.com/set1/input7.png",
					"./res/cnn/chess.com/set1/input8.png",
					"./res/cnn/chess.com/set1/input9.png",
					"./res/cnn/chess.com/set1/input10.png",*/
					
					/*"./res/cnn/lichess.org/set1/input1.png",
					"./res/cnn/lichess.org/set1/input2.png",
					"./res/cnn/lichess.org/set1/input3.png",
					"./res/cnn/lichess.org/set1/input4.png",
					"./res/cnn/lichess.org/set1/input5.png",
					"./res/cnn/lichess.org/set1/input6.png",
					"./res/cnn/lichess.org/set1/input7.png",
					"./res/cnn/lichess.org/set1/input8.png",
					"./res/cnn/lichess.org/set1/input9.png",
					"./res/cnn/lichess.org/set1/input10.png",*/
					
					/*"./res/cnn/chess24.com/set1/input1.png",
					"./res/cnn/chess24.com/set1/input2.png",
					"./res/cnn/chess24.com/set1/input3.png",
					"./res/cnn/chess24.com/set1/input4.png",
					"./res/cnn/chess24.com/set1/input5.png",
					"./res/cnn/chess24.com/set1/input6.png",
					"./res/cnn/chess24.com/set1/input7.png",
					"./res/cnn/chess24.com/set1/input8.png",
					"./res/cnn/chess24.com/set1/input9.png",
					"./res/cnn/chess24.com/set1/input10.png",
					"./res/cnn/chess24.com/set1/input11.png",*/
					
					"./res/cnn/books/set1/input1.png",
					"./res/cnn/books/set1/input2.png",
					"./res/cnn/books/set1/input3.png",
					"./res/cnn/books/set1/input4.png",
					"./res/cnn/books/set1/input5.png",
					"./res/cnn/books/set1/input6.png",
					"./res/cnn/books/set1/input7.png",
					"./res/cnn/books/set1/input8.png",
					"./res/cnn/books/set1/input9.png",
					"./res/cnn/books/set1/input10.png",
					"./res/cnn/books/set1/input11.png",
				};
			
			
			BoardProperties boardProperties = new BoardProperties(256);
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs_RGB(boardProperties, inputFiles, "./datasets_deepnetts/dataset_books_set_1_inverted/");
			
			final List<Object> images = new ArrayList<Object>();
			final List<Integer> pids = new ArrayList<Integer>();
			
			for (int i = 0; i < pairs.length; i++) {
				images.addAll(pairs[i].getImages());
				pids.addAll(pairs[i].getPIDs());
			}
			
			/*for (int i = 0; i < images.size(); i++) {
				int[][] matrix = (int[][]) images.get(i);
				BufferedImage image = ScannerUtils.createGrayImage(matrix);
				int pid = pids.get(i);
				File curDir = new File(genDir, "" + pid);
				curDir.mkdirs();
				
				File imageFile = new File(curDir, "" + System.currentTimeMillis() + ".png");
				ImageIO.write(image, "png", imageFile);
				
				Thread.currentThread().sleep(3);
			}*/
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
