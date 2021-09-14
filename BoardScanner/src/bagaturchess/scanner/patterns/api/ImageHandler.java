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
package bagaturchess.scanner.patterns.api;


import org.opencv.core.Mat;

import java.io.IOException;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.FilterInfo;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.MatrixUtils.PatternMatchingData;


/**
 * Handles porting to awt and to android with different implementations
 */
public interface ImageHandler {
	public Object loadImageFromFS(Object path) throws IOException;
	public Object resizeImage(Object source, int newsize);
	public void saveImage(String fileName, String formatName, Object image) throws IOException;
	public int[][] convertToGrayMatrix(Object image);
	public Object createGrayImage(int[][] matrix);
	public Object loadPieceImageFromMemory(int pid, String piecesSetName, int size);
	public void printInfo(int[][] source, MatrixUtils.PatternMatchingData matcherData, String fileName);
	public void printInfo(MatrixUtils.PatternMatchingData matcherData, String fileName);
	public int[][] createSquareImage(int bgcolor, int size);
	public Object createPieceImage(String pieceSetName, int pid, int bgcolor, int size);
	public Object createPieceImage_Gray(String pieceSetName, int pid, int bgcolor, int size);
	public Object createBoardImage(BoardProperties boardProperties, String fen, Object whiteSquareColor, Object blackSquareColor);
	public Object getColor(int grayColor);
	public Object enlarge(Object image, double scale, Object bgcolor);
	public Object getAVG(Object image);
	public Object extractResult(Object image, PatternMatchingData bestData, float factorOfExtension);
	public Object extractResult(Object image, FilterInfo filterInfo, float factorOfExtension);
	public Object rotateImageByDegrees(Object image, float angle);
	public Mat graphic2Mat(Object image) throws IOException;
	public Object mat2Graphic(Mat matrix) throws IOException;
	public void releaseGraphic(Object image) throws IOException;
}
