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
package bagaturchess.scanner.machinelearning.dataset;


import java.awt.image.BufferedImage;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.Color;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.utils.ScannerUtils;


public class DataSetInitPair_ByPiecesSetAndSquareColor_RGB extends DataSetInitPair {
	
	
	private BoardProperties boardProperties;
	
	private Color lightSquare;
	private Color darkSquare;
	
	private String dirToSave;
	
	
	DataSetInitPair_ByPiecesSetAndSquareColor_RGB(BoardProperties _imageProperties, Color _lightSquare, Color _darkSquare, String _dirToSave) {
		
		super();
		
		boardProperties = _imageProperties;
		
		lightSquare = _lightSquare;
		darkSquare = _darkSquare;
		
		dirToSave = _dirToSave;
		
		for (int pid = 0; pid <= 12; pid++) {
			
			for (double pieceScale = 1.25; pieceScale >= 0.75; pieceScale -= 0.01) {
				
				BufferedImage whiteImage = ScannerUtils.createPieceImage(boardProperties, pid, new java.awt.Color(lightSquare.red, lightSquare.green, lightSquare.blue), pieceScale);
				BufferedImage blackImage = ScannerUtils.createPieceImage(boardProperties, pid, new java.awt.Color(darkSquare.red, darkSquare.green, darkSquare.blue), pieceScale);
				
				images.add(MatrixUtils.invertImage(MatrixUtils.normalizeMatrix(ScannerUtils.convertToRGBMatrix(whiteImage))));
				images.add(MatrixUtils.invertImage(MatrixUtils.normalizeMatrix(ScannerUtils.convertToRGBMatrix(blackImage))));
				
				if (pid == 0) {
					pids.add(0);
					pids.add(0);
				} else {
					pids.add(pid);
					pids.add(pid);
				}
				
				if (dirToSave != null) {
					int index1 = images.size() - 1;
					BufferedImage lightImage = ScannerUtils.createRGBImage((int[][][])images.get(index1));
					ScannerUtils.saveImage("" + index1 + "_" + System.nanoTime(), lightImage, "png", dirToSave + pids.get(index1) + "/");
					
					int index2 = images.size() - 2;
					BufferedImage darkImage = ScannerUtils.createRGBImage((int[][][])images.get(index2));
					ScannerUtils.saveImage("" + index2 + "_" + System.nanoTime(), darkImage, "png", dirToSave + pids.get(index2) + "/");
				}
			}
		}
	}
}
