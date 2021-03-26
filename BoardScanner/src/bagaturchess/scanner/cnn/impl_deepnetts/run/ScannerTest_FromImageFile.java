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
package bagaturchess.scanner.cnn.impl_deepnetts.run;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.compute.ProbabilitiesCalculator;
import bagaturchess.scanner.cnn.compute.ProbabilitiesCalculator_Gray;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.model.NetworkModel;
import bagaturchess.scanner.cnn.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import deepnetts.net.ConvolutionalNetwork;


public class ScannerTest_FromImageFile {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			//NetworkModel netmodel = new NetworkModel_Gray("scanner.bin", boardProperties.getSquareSize());
			//NetworkModel netmodel = new NetworkModel_Gray("scanner.chesscom.bin", boardProperties.getSquareSize());
			NetworkModel<ConvolutionalNetwork> netmodel = new NetworkModel_Gray(new FileInputStream("scanner.lichessorg.bin"), boardProperties.getSquareSize());
			ProbabilitiesCalculator scanner = new ProbabilitiesCalculator_Gray(netmodel);
			
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/lichess.org/test8.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./res/cnn/lichess.org/set1/input1.png"));
			BufferedImage boardImage = ImageIO.read(new File("./res/cnn/chess.com/set1/input1.png"));
			boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
			int[][] boardMatrix= ScannerUtils.convertToGrayMatrix(boardImage);
			
			double probability = scanner.getAccumulatedProbability(boardMatrix);
			
			System.out.println(probability);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
