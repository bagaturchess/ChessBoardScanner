/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.patterns.impl1.preprocess;


import java.io.IOException;
import java.util.Set;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.KMeansPixels;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


//As it works slowly I will have a look at the links below later
//https://stackoverflow.com/questions/13390238/jtransforms-fft-on-image
//matched filter in signal processing
//https://stackoverflow.com/questions/12598818/finding-a-picture-in-a-picture-with-java
//https://stackoverflow.com/questions/42597094/cross-correlation-with-signals-of-different-lengths-in-java
//https://stackoverflow.com/questions/13445497/correlation-among-2-images
public class ImagePreProcessor_Crop extends ImagePreProcessor_Base {
	
	
	private static final double SIZE_DELTA_PERCENT = 0.05;
	
	
	public ImagePreProcessor_Crop(BoardProperties _boardProperties) {
		super(_boardProperties);
	}
	
	
	public Object filter(Object image) throws IOException {
		
		image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties.getImageSize());
		int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(image);
		ImageHandlerSingleton.getInstance().saveImage("Crop_board_input", "png", image);

		
		ResultPair<Integer, Integer> squareColors = MatrixUtils.getSquaresColor(grayBoard);
		
		Object whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(squareColors.getFirst());
		Object blackSquareColor = ImageHandlerSingleton.getInstance().getColor(squareColors.getSecond());
		
		//Set<Integer> emptySquares = MatrixUtils.getEmptySquares(grayBoard);
		//ResultPair<Integer, Integer> bgcolours = MatrixUtils.getSquaresColor(grayBoard, emptySquares);
		//Object whiteSquareColor = ImageHandlerSingleton.getInstance().getColor(bgcolours.getFirst());
		//Object blackSquareColor = ImageHandlerSingleton.getInstance().getColor(bgcolours.getSecond());
		
		Object emptyBoard = ImageHandlerSingleton.getInstance().createBoardImage(boardProperties, "8/8/8/8/8/8/8/8", whiteSquareColor, blackSquareColor);
		ImageHandlerSingleton.getInstance().saveImage("Crop_board_empty", "png", emptyBoard);
				
		//int bgcolor = MatrixUtils.getAVG(grayBoard);
		
		MatrixUtils.PatternMatchingData bestData = null;
		int maxSize = grayBoard.length;
		int startSize = (int) ((1 - SIZE_DELTA_PERCENT) * maxSize);
		Object[] cache = new Object[maxSize + 1];
		
		for (int size = startSize; size <= maxSize; size++) {
			
			int[][] grayPattern = cache[size] != null ? (int[][]) cache[size] : ImageHandlerSingleton.getInstance().convertToGrayMatrix(ImageHandlerSingleton.getInstance().resizeImage(emptyBoard, size));
			cache[size] = grayPattern;
			
			MatrixUtils.PatternMatchingData curData = MatrixUtils.matchImages(grayBoard, grayPattern);
			
			//System.out.println(curData.size + " " + curData.delta);
			
			if (bestData == null || bestData.delta > curData.delta) {
				bestData = curData;
			}
		}
		
		//System.out.println("BEST size " + bestData.size);
		//ImageHandlerSingleton.getInstance().saveImage("Crop_board_pattern", "png", ImageHandlerSingleton.getInstance().createGrayImage(bestData.pattern));
		
		Object result = ImageHandlerSingleton.getInstance().extractResult(image, bestData, 1.05f);
		//result = ImageHandlerSingleton.getInstance().enlarge(result, 1.03f, ImageHandlerSingleton.getInstance().getAVG(result));
		result = ImageHandlerSingleton.getInstance().resizeImage(result, boardProperties.getImageSize());
		ImageHandlerSingleton.getInstance().saveImage("Crop_board_result", "png", result);
		
		return result;
	}
}
