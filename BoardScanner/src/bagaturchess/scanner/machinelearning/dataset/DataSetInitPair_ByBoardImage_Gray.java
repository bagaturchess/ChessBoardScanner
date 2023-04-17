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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.utils.ScannerUtils;

import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.EMPTY;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;


public class DataSetInitPair_ByBoardImage_Gray extends DataSetInitPair {
	
	
	private static final double TRANSLATIONS_RATIO = 0.01;
	
	
	private String dirToSave;
	
	
	public DataSetInitPair_ByBoardImage_Gray(BufferedImage boardImage, String FEN, String _dirToSave, boolean extend) {
		
		super();
		
		dirToSave = _dirToSave;
		
		//ScannerUtils.saveImage(fileName + "_grayed", boardImage, "png");
		
		int[][] matrixOfInitialBoard = ScannerUtils.convertToGrayMatrix(boardImage);
		
		ResultPair<Integer, Integer> lightAndDarkSquaresColors = MatrixUtils.getSquaresColor_Gray(matrixOfInitialBoard);
		
		matrixOfInitialBoard = MatrixUtils.normalizeMatrix(matrixOfInitialBoard);
		
		IBitBoard board = BoardUtils.createBoard_WithPawnsCache(FEN);
		
		Map<Integer, int[][]> result = MatrixUtils.splitTo64Squares(matrixOfInitialBoard);
		
		for (Integer square_id : result.keySet()) {
			
			int[][] graySquare = result.get(square_id);
			
			List<int[][]> translations = new ArrayList<int[][]>();
			
			translations.add(graySquare);
			
			int fillColour = (int) (256d * Math.random());
			
			/*int fillColour = lightAndDarkSquaresColors.getSecond().intValue();
			int avg_color = MatrixUtils.getAVG(graySquare);
			if (Math.abs(avg_color - lightAndDarkSquaresColors.getFirst().intValue()) <=  Math.abs(avg_color - lightAndDarkSquaresColors.getSecond().intValue())) {
				fillColour = lightAndDarkSquaresColors.getFirst().intValue();
				
			}
			*/
			
			if (extend) translations.addAll(MatrixUtils.generateShifts(graySquare, TRANSLATIONS_RATIO, fillColour));
			
			//System.out.println(translations.size());
			
			for (int[][] matrix : translations) {
				
				int size_old = pids.size();
				
				int piece_type = board.getFigureType(square_id);
				int piece_color = board.getFigureColour(square_id);
				
				switch (piece_type) {
					case PAWN:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_PAWN : Constants.PID_B_PAWN);
						break;
					case NIGHT:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_KNIGHT : Constants.PID_B_KNIGHT);
						break;
					case BISHOP:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_BISHOP : Constants.PID_B_BISHOP);
						break;
					case ROOK:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_ROOK : Constants.PID_B_ROOK);
						break;
					case QUEEN:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_QUEEN : Constants.PID_B_QUEEN);
						break;
					case KING:
						images.add(matrix);
						pids.add(piece_color == Constants.COLOUR_WHITE ? Constants.PID_W_KING : Constants.PID_B_KING);
						break;
					case EMPTY:
						images.add(matrix);
						pids.add(Constants.PID_NONE);
						break;
					default:
						throw new IllegalStateException("piece_type=" + piece_type);
				}
				
				
				if (pids.size() == size_old) {
					throw new IllegalStateException("pids.size() == size_old");
				}
				
				
				if (dirToSave != null) {
					BufferedImage image = ScannerUtils.createGrayImage(matrix);
					System.out.println("Saving " + "" + size_old + "_" + System.nanoTime() + " in " + dirToSave + pids.get(size_old) + "/");
					ScannerUtils.saveImage("" + size_old + "_" + System.nanoTime(), image, "png", dirToSave + pids.get(size_old) + "/");
				}
			}
		}
	}
}
