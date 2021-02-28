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


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.scanner.cnn.impl_deepnetts.BoardScanner;
import bagaturchess.scanner.cnn.impl_deepnetts.BoardScanner_RGB;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_RGB;
import bagaturchess.scanner.cnn.impl_deepnetts.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class ScannerTest_FromGeneratedImage {
	
	
	private static final String NET_FILE = "scanner.cnn.set1.bin";
	
	
	public static void main(String[] args) {
		try {
			
			IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache();
			
			BoardProperties boardProperties = new BoardProperties(256, "set1");
			BufferedImage boardImage = (BufferedImage) ImageHandlerSingleton.getInstance().createBoardImage(boardProperties, bitboard.toEPD(), new Color(220, 220, 220), new Color(120, 120, 120));
			//ScannerUtils.saveImage("board", boardImage);
			
			NetworkModel netmodel = new NetworkModel_RGB(new FileInputStream(NET_FILE), boardProperties.getSquareSize());
			BoardScanner scanner = new BoardScanner_RGB(netmodel);
			
			String fen = scanner.scan(ScannerUtils.convertToRGBMatrix(boardImage));
			
			System.out.println(fen);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
