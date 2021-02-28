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
package bagaturchess.scanner.patterns.impl;


import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.cnn.impl_dn.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.MatrixUtils;


public class ColorAdjustmentTester {
	
	
	public static void main(String[] args) {
		
		try {
			
			BufferedImage image = ImageIO.read(new File("./data/tests/square2.png"));
			image = ScannerUtils.resizeImage(image, Math.min(image.getHeight(), image.getWidth()));
			int[][] grayImage = ScannerUtils.convertToGrayMatrix(image);
			
			BufferedImage resultImage = ScannerUtils.createGrayImage(grayImage);
			ScannerUtils.saveImage("input", resultImage, "png");
			
			//System.out.println("size is " + grayImage.length);
			
			BoardProperties boardProperties = new BoardProperties(632);
			int bgcolor = MatrixUtils.getAVG(grayImage);
			
			int[][] pieceImage = ScannerUtils.createPieceImage(boardProperties.getPiecesSetFileNamePrefix(), Constants.PID_B_KNIGHT, bgcolor, boardProperties.getSquareSize());
			BufferedImage resultImage1 = ScannerUtils.createGrayImage(pieceImage);
			ScannerUtils.saveImage("step1", resultImage1, "png");
			
			pieceImage = ScannerUtils.createPieceImage(boardProperties, Constants.PID_B_KNIGHT, bgcolor, bgcolor, boardProperties.getSquareSize());
			BufferedImage resultImage2 = ScannerUtils.createGrayImage(pieceImage);
			ScannerUtils.saveImage("step2", resultImage2, "png");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
