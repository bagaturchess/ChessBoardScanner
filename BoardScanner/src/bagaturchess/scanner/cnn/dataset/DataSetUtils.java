package bagaturchess.scanner.cnn.dataset;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;


public class DataSetUtils {
	
	
	public static DataSetInitPair[] getInitPairs_Gray(BoardProperties boardProperties, String[] fileNames) throws IOException {
		DataSetInitPair[] result = new DataSetInitPair[fileNames.length];
		for (int i = 0; i < result.length; i++) {			
			result[i] = getInitPair_Gray(boardProperties, fileNames[i]);
		}
		return result;
	}
	
	
	private static DataSetInitPair getInitPair_Gray(BoardProperties boardProperties, String fileName) throws IOException {
		
		BufferedImage boardImage = ImageIO.read(new File(fileName));
		boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
		
		DataSetInitPair pair = new DataSetInitPair_ByBoardImage_Gray(boardImage);
		
		return pair;
	}
	
	
	public static DataSetInitPair[] getInitPairs_RGB(BoardProperties boardProperties, String[] fileNames, String genDir) throws IOException {
		DataSetInitPair[] result = new DataSetInitPair[fileNames.length];
		for (int i = 0; i < result.length; i++) {			
			result[i] = getInitPair_RGB(boardProperties, fileNames[i], genDir);
		}
		return result;
	}
	
	
	private static DataSetInitPair getInitPair_RGB(BoardProperties boardProperties, String fileName, String genDir) throws IOException {
		
		BufferedImage boardImage = ImageIO.read(new File(fileName));
		boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
		
		DataSetInitPair pair = new DataSetInitPair_ByBoardImage_RGB(boardImage, genDir);
		
		return pair;
	}
}
