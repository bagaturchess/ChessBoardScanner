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
package bagaturchess.scanner.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bagaturchess.bitboard.impl.utils.VarStatistic;


public class MatrixUtils {
	
	
	public static int getAVG(int[][] grayImage) {
		
		if (grayImage.length != grayImage[0].length) {
			throw new IllegalStateException();
		}
		
		long gray = 0;
		long count = 0;
        for (int i = 0; i < grayImage.length; i++) { 
            for (int j = 0; j < grayImage.length; j++) {
            	gray += grayImage[i][j];
				count++;
            }
        }
        
        return (int) (gray / count);
	}
	
	
	public static Set<Integer> getEmptySquares(int[][] grayBoard) {
		return getEmptySquares(grayBoard, 0.9d);
		
	}
	
	
	private static Set<Integer> getEmptySquares(int[][] grayBoard, double emptySquareThreshold) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		boolean[] markedForEmpty = new boolean[64];
		VarStatistic colorDeviations = new VarStatistic(false);
		Map<Integer, VarStatistic> squaresStats = new HashMap<Integer, VarStatistic>();
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int fieldID = 63 - (file + 8 * rank);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				
				KMeansPixels kmeans = new KMeansPixels(3, squareMatrix);
				int maxWeightCentroidID = kmeans.getMaxWeightIndex();
				int maxWeight = kmeans.weights[maxWeightCentroidID];
				if(maxWeight >= emptySquareThreshold * squareMatrix.length * squareMatrix.length) {
					markedForEmpty[fieldID] = true;
				}
				VarStatistic squareStat = calculateColorStats(squareMatrix, -1); 
				
				squaresStats.put(fieldID, squareStat);
				
				colorDeviations.addValue(squareStat.getDisperse(), squareStat.getDisperse());
			}
		}
		
		for (Integer fieldID: squaresStats.keySet()) {
			VarStatistic squareStat = squaresStats.get(fieldID);
			if (markedForEmpty[fieldID]) {
				emptySquaresIDs.add(fieldID);
			} else if (squareStat.getDisperse() < colorDeviations.getEntropy() - colorDeviations.getDisperse() / 3) {
				emptySquaresIDs.add(fieldID);
			}
		}
		
		return emptySquaresIDs;
	}
	
	
	public static Set<Integer> getEmptySquares_Heuristic1(int[][] grayBoard, double emptySquareThreshold) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		boolean[] markedForEmpty = new boolean[64];
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int squareID = 63 - (file + 8 * rank);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				
				KMeansPixels kmeans = new KMeansPixels(3, squareMatrix);
				int maxWeightCentroidID = kmeans.getMaxWeightIndex();
				int maxWeight = kmeans.weights[maxWeightCentroidID];
				if(maxWeight >= emptySquareThreshold * squareMatrix.length * squareMatrix.length) {
					markedForEmpty[squareID] = true;
				}
			}
		}
		
		for (int squareID = 0; squareID < 64; squareID++) {
			if (markedForEmpty[squareID]) {
				emptySquaresIDs.add(squareID);
			}
		}
		
		return emptySquaresIDs;
	}
	
	
	public static Set<Integer> getEmptySquares_Heuristic1(int[][][] rgbBoard, double emptySquareThreshold) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		int[][] rBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] gBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] bBoard = new int[rgbBoard.length][rgbBoard[0].length];
		
        for (int i = 0; i < rgbBoard.length; i++ ) {
            for (int j = 0; j < rgbBoard[0].length; j++ ) {
            	rBoard[i][j] = rgbBoard[i][j][0];
            	gBoard[i][j] = rgbBoard[i][j][1];
            	bBoard[i][j] = rgbBoard[i][j][2];
            }
        }
        
        
		emptySquaresIDs.addAll(getEmptySquares_Heuristic1(rBoard, emptySquareThreshold));
		emptySquaresIDs.addAll(getEmptySquares_Heuristic1(gBoard, emptySquareThreshold));
		emptySquaresIDs.addAll(getEmptySquares_Heuristic1(bBoard, emptySquareThreshold));

		
		return emptySquaresIDs;
	}
	
	
	public static Set<Integer> getEmptySquares_Heuristic2(int[][] grayBoard) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		double[] colorDeviations = new double[64];
		for (int i = 0; i < grayBoard.length; i += grayBoard.length / 8) {
			for (int j = 0; j < grayBoard.length; j += grayBoard.length / 8) {
				
				int file = i / (grayBoard.length / 8);
				int rank = j / (grayBoard.length / 8);
				int squareID = 63 - (file + 8 * rank);
				
				int[][] squareMatrix = MatrixUtils.getSquarePixelsMatrix(grayBoard, i, j);
				
				VarStatistic squareStat = calculateColorStats(squareMatrix, -1);
				
				colorDeviations[squareID] = squareStat.getDisperse();
			}
		}
		
		KMeansScalar kmeans = new KMeansScalar(11, colorDeviations);
		for (int squareID = 0; squareID < 64; squareID++) {
			if (kmeans.centroids_ids[squareID] <= 0) {
				emptySquaresIDs.add(squareID);
			}
		}
		
		return emptySquaresIDs;
	}
	
	
	public static Set<Integer> getEmptySquares_Heuristic2(int[][][] rgbBoard) {
		
		Set<Integer> emptySquaresIDs = new HashSet<Integer>();
		
		int[][] rBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] gBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] bBoard = new int[rgbBoard.length][rgbBoard[0].length];
		
        for (int i = 0; i < rgbBoard.length; i++ ) {
            for (int j = 0; j < rgbBoard[0].length; j++ ) {
            	rBoard[i][j] = rgbBoard[i][j][0];
            	gBoard[i][j] = rgbBoard[i][j][1];
            	bBoard[i][j] = rgbBoard[i][j][2];
            }
        }
        
        
		emptySquaresIDs.addAll(getEmptySquares_Heuristic2(rBoard));
		emptySquaresIDs.addAll(getEmptySquares_Heuristic2(gBoard));
		emptySquaresIDs.addAll(getEmptySquares_Heuristic2(bBoard));

		
		return emptySquaresIDs;
	}
	
	
	public static ResultPair<Integer, Integer> getSquaresColor_Gray(int[][] grayBoard) {
		
		KMeansPixels kmeans = new KMeansPixels(4, grayBoard);
		int[] clustersIndexes = kmeans.get2MaxWeightsIndexes();
		
		int gray1 = 255;
		int gray2 = 0;
		
		//int[][] boardPixels_c1 = new int[grayBoard.length][grayBoard.length];
		//int[][] boardPixels_c2 = new int[grayBoard.length][grayBoard.length];
		for (int index = 0; index < clustersIndexes.length; index++) {
			int centoridID = clustersIndexes[index];
			for (int i = 0; i < grayBoard.length; i++) {
				for (int j = 0; j < grayBoard.length; j++) {
					if (centoridID == kmeans.centroids_ids[i][j]) {
						if (index == 0) {
							//boardPixels_c1[i][j] = grayBoard[i][j];
							gray1 = kmeans.centroids_values[centoridID];
						} else {
							//boardPixels_c2[i][j] = grayBoard[i][j];
							gray2 = kmeans.centroids_values[centoridID];
						}
					}
				}
			}
		}
				
		return new ResultPair<Integer, Integer>(Math.max(gray1, gray2), Math.min(gray1, gray2));
	}
	
	
	public static ResultPair<Color, Color> getSquaresColor_RGB(int[][][] rgbBoard) {
		
		int[][] rBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] gBoard = new int[rgbBoard.length][rgbBoard[0].length];
		int[][] bBoard = new int[rgbBoard.length][rgbBoard[0].length];
		
        for (int i = 0; i < rgbBoard.length; i++ ) {
            for (int j = 0; j < rgbBoard[0].length; j++ ) {
            	rBoard[i][j] = rgbBoard[i][j][0];
            	gBoard[i][j] = rgbBoard[i][j][1];
            	bBoard[i][j] = rgbBoard[i][j][2];
            }
        }
		
        ResultPair<Integer, Integer> red = getSquaresColor_Gray(rBoard);
        ResultPair<Integer, Integer> green = getSquaresColor_Gray(gBoard);
        ResultPair<Integer, Integer> blue = getSquaresColor_Gray(bBoard);
        
		return new ResultPair<Color, Color>(new Color(red.getFirst(), green.getFirst(), blue.getFirst()), new Color(red.getSecond(), green.getSecond(), blue.getSecond()));
	}
	
	
	public static VarStatistic calculateColorStats(int[][] grayMatrix, int skipValue) {
		
		VarStatistic stat = new VarStatistic(false);
		
		for (int i = 0; i < grayMatrix.length; i++) {
			for (int j = 0; j < grayMatrix.length; j++) {
				int cur = grayMatrix[i][j];
				if (cur != skipValue) {
					stat.addValue(cur, cur);
				}
			}
		}
		
		return stat;
	}
	
	
	public static int[][] normalizeMatrix(int[][] grayMatrix) {
		int[][] result = new int[grayMatrix.length][grayMatrix.length];
		
		VarStatistic stats = calculateColorStats(grayMatrix, -1);
		
		//System.out.println("normalizeMatrix: mean=" + stats.getEntropy() + " deviation=" + stats.getDisperse());
		
		for (int i = 0; i < grayMatrix.length; i++) {
			for (int j = 0; j < grayMatrix.length; j++) {
				
				int cur = grayMatrix[i][j];
				
				result[i][j] = (int) (cur * (127 / stats.getEntropy()));
				
				if (result[i][j] < 0) {
					result[i][j] = 0;
				}
				
				if (result[i][j] > 255) {
					result[i][j] = 255;
				}
			}
		}
		
		return result;
	}
	
	
	public static int[][][] normalizeMatrix(int[][][] rgbMatrix) {	
		
		int[][] rBoard = new int[rgbMatrix.length][rgbMatrix[0].length];
		int[][] gBoard = new int[rgbMatrix.length][rgbMatrix[0].length];
		int[][] bBoard = new int[rgbMatrix.length][rgbMatrix[0].length];
		
        for (int i = 0; i < rgbMatrix.length; i++ ) {
            for (int j = 0; j < rgbMatrix[0].length; j++ ) {
            	rBoard[i][j] = rgbMatrix[i][j][0];
            	gBoard[i][j] = rgbMatrix[i][j][1];
            	bBoard[i][j] = rgbMatrix[i][j][2];
            }
        }
		
        rBoard = normalizeMatrix(rBoard);
        gBoard = normalizeMatrix(gBoard);
        bBoard = normalizeMatrix(bBoard);
        
		int[][][] result = new int[rgbMatrix.length][rgbMatrix[0].length][rgbMatrix[0][0].length];
        
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result.length; j++) {
				result[i][j][0] = rBoard[i][j];
				result[i][j][1] = gBoard[i][j];
				result[i][j][2] = bBoard[i][j];
			}
		}
		
		return result;
	}
	
	
	public static int[][][] convertTo3ChannelsGray(int[][] grayMatrix) {
		
		int[][][] result = new int[grayMatrix.length][grayMatrix.length][3];
		
		for (int i = 0; i < grayMatrix.length; i++) {
			for (int j = 0; j < grayMatrix.length; j++) {
				
				int gray_color = grayMatrix[i][j];
				
				result[i][j][0] = gray_color;
				result[i][j][1] = gray_color;
				result[i][j][2] = gray_color;
			}
		}
		
		return result;
	}
	
	
	public static int[][][] convertTo3ChannelsGray(int[][][] rgbMatrix) {
		
		int[][][] result = new int[rgbMatrix.length][rgbMatrix.length][3];
		
		for (int i = 0; i < rgbMatrix.length; i++) {
			for (int j = 0; j < rgbMatrix.length; j++) {
				
				//int value = (rgbMatrix[i][j][0] + rgbMatrix[i][j][1] + rgbMatrix[i][j][2]) / 3;
				int value = (int) (rgbMatrix[i][j][0] * 0.2989d + rgbMatrix[i][j][1] * 0.5870 + rgbMatrix[i][j][2] * 0.1142);
				
				result[i][j][0] = value;
				result[i][j][1] = value;
				result[i][j][2] = value;
			}
		}
		
		return result;
	}
	
	
	public static final int[][][] invertImage(int[][][] rgbMatrix) {
		
		int[][][] result = new int[rgbMatrix.length][rgbMatrix.length][3];
		
		for (int i = 0; i < rgbMatrix.length; i++) {
			for (int j = 0; j < rgbMatrix.length; j++) {
				
				result[i][j][0] = 255 - rgbMatrix[i][j][0];
				result[i][j][1] = 255 - rgbMatrix[i][j][1];
				result[i][j][2] = 255 - rgbMatrix[i][j][2];
			}
		}
		
		return result;
	}
	
	
	public static Map<Integer, int[][]> splitTo64Squares(int[][] matrix) {
		
		int size = matrix.length;
		if (matrix.length != matrix[0].length) {
			throw new IllegalStateException("matrix is not square");
		}
		
		Map<Integer, int[][]> result = new HashMap<Integer, int[][]>();
		for (int i = 0; i < size; i += size / 8) {
			for (int j = 0; j < size; j += size / 8) {
				int file = i / (size / 8);
				int rank = j / (size / 8);
				int filedID = 63 - (file + 8 * rank);
				int[][] arr = getSquarePixelsMatrix(matrix, i, j);
				result.put(filedID, arr);
			}
		}
		
		return result;
	}
	
	
	public static Map<Integer, int[][][]> splitTo64Squares(int[][][] matrix) {
		
		int size = matrix.length;
		if (matrix.length != matrix[0].length) {
			throw new IllegalStateException("matrix is not square");
		}
		
		if (matrix[0][0].length != 3) {
			throw new IllegalStateException("There is no 3 channels");
		}
		
		Map<Integer, int[][][]> result = new HashMap<Integer, int[][][]>();
		for (int i = 0; i < size; i += size / 8) {
			for (int j = 0; j < size; j += size / 8) {
				int file = i / (size / 8);
				int rank = j / (size / 8);
				int filedID = 63 - (file + 8 * rank);
				int[][][] arr = getSquarePixelsMatrix(matrix, i, j);
				result.put(filedID, arr);
			}
		}
		
		return result;
	}
	
	
	public static int[][] getSquarePixelsMatrix(int[][] matrix, int i1, int j1) {
		
		if (matrix.length % 8 != 0) {
			throw new IllegalStateException("size is not devidable by 8");
		}
		
		int size = matrix.length / 8;
		int[][] result = new int[size][size];
		
		int ic = 0;
		for (int i = i1; i < i1 + size; i++) {
			int jc = 0;
			for (int j = j1; j < j1 + size; j++) {
				result[ic][jc] = matrix[i][j];
				jc++;
			}
			ic++;
		}
		
		return result;
	}
	
	
	public static int[][][] getSquarePixelsMatrix(int[][][] matrix, int i1, int j1) {
		
		if (matrix.length % 8 != 0) {
			throw new IllegalStateException("size is not devidable by 8");
		}
		
		int size = matrix.length / 8;
		int[][][] result = new int[size][size][3];
		
		int ic = 0;
		for (int i = i1; i < i1 + size; i++) {
			int jc = 0;
			for (int j = j1; j < j1 + size; j++) {
				result[ic][jc][0] = matrix[i][j][0];
				result[ic][jc][1] = matrix[i][j][1];
				result[ic][jc][2] = matrix[i][j][2];
				jc++;
			}
			ic++;
		}
		
		return result;
	}
	
	
	public static List<int[][]> generateTranslations(int[][] matrix, int radius) {
		
		List<int[][]> result = new ArrayList<int[][]>();
		
		Set<Translation> translations = generateCirclePixels(radius);
		
		for (Translation translation : translations) {
			if (translation.x != 0 || translation.y != 0) {
				result.add(moveWithXY(matrix, translation.x, translation.y, -1));
			}
		}
		
		return result;
	}
	
	
	private static Set<Translation> generateCirclePixels(int radius) {
		
		Set<Translation> result = new HashSet<Translation>();
		
	    double PI = 3.1415926535;
	    for (double angle = 0; angle < 360; angle++) {
	        double x = radius * Math.cos(angle * PI / 180);
	        double y = radius * Math.sin(angle * PI / 180);

	        result.add(new Translation((int)x, (int)y));
	    }
	    
	    return result;
	}
	
	
	public static List<int[][]> generateShifts(int[][] matrix, double ratio, int fillColor) {
		
		List<int[][]> result = new ArrayList<int[][]>();
		
		Set<Translation> translations = generateShifts(ratio, matrix.length, matrix.length);
		
		for (Translation translation : translations) {
			if (translation.x != 0 || translation.y != 0) {
				result.add(moveWithXY(matrix, translation.x, translation.y, fillColor));
			}
		}
		
		return result;
	}
	
	
	private static Set<Translation> generateShifts(double ratio, int width_org, int height_org) {
		
		Set<Translation> result = new HashSet<Translation>();
		
		for (int x = - (int) (ratio * width_org); x <= (int) (ratio * width_org); x++) {
			for (int y = - (int) (ratio * height_org); y <= (int) (ratio * height_org); y++) {
				result.add(new Translation((int)x, (int)y));
			}
		}
	    
	    return result;
	}
	
	
	public static final int[][] rotateMatrix(int[][] source, float angleInDegrees, int bgcolor) {
		
		double angleInRadians = (angleInDegrees * Math.PI / 180);
		
		int resultSize = (int) (source.length * (1 + Math.abs((Math.tan(angleInRadians)))));
		
		int[][] result = new int[resultSize][resultSize];
		for (int x = 0; x < result.length; x++) {
			for (int y = 0; y < result.length; y++) {
				result[x][y] = bgcolor;
			}
		}
		
		int centerX = source.length / 2;
		int centerY = source.length / 2;
		
		for (int x = 0; x < source.length; x++) {
			for (int y = 0; y < source.length; y++) {
				int x1 = (int) (centerX + (x-centerX) * Math.cos(angleInRadians) - (y-centerY) * Math.sin(angleInRadians));
				int y1 = (int) (centerY + (x-centerX) * Math.sin(angleInRadians) + (y-centerY) * Math.cos(angleInRadians));
				result[(int) Math.floor(0.5 + x1 + (resultSize - source.length) / 2)][(int) Math.floor(0.5 + y1 + (resultSize - source.length) / 2)] = source[x][y];
			}
		}
		
		return result;
	}
	
	
	public static final PatternMatchingData matchImages(int[][] graySource, int[][] grayPattern) {
		
		PatternMatchingData result = null;
		
		for (int x = 0; x <= graySource.length - grayPattern.length; x++) {
		    for (int y = 0; y <= graySource.length - grayPattern.length; y++) {
		        
		    	PatternMatchingData cur = new PatternMatchingData();
		    	cur.x = x;
		    	cur.y = y;
		    	cur.size = grayPattern.length;
		    	cur.pattern_gray = grayPattern;
		    	
		    	int count = 0;
		        for (int i = 0; i < grayPattern.length; i++ ) {
		            for (int j = 0; j < grayPattern.length; j++ ) {
		                
		                cur.delta += Math.abs(graySource[x+i][y+j] - grayPattern[i][j]);
		                count++;
		                
		                /*if (result != null && cur.delta / (float) (count) > result.delta) {
		                	i = grayPattern.length;
		                	break;
		                }*/
		            }
		        }
		        cur.delta = cur.delta / (double) (count); 
		        
		        if (result == null || result.delta > cur.delta) { 
		        	result = cur;
		        }
		    }
		}
			
		return result;
	}
	
	
	public static final PatternMatchingData matchImages(int[][][] rgbSource, int[][][] rgbPattern) {
		
		PatternMatchingData result = null;
		
		for (int x = 0; x <= rgbSource.length - rgbPattern.length; x++ ) {
		    for (int y = 0; y <= rgbSource.length - rgbPattern.length; y++ ) {
		        
		    	PatternMatchingData cur = new PatternMatchingData();
		    	cur.x = x;
		    	cur.y = y;
		    	cur.size = rgbPattern.length;
		    	
		    	int count = 0;
		        for (int i = 0; i < rgbPattern.length; i++ ) {
		            for (int j = 0; j < rgbPattern.length; j++ ) {
		            	
		                int pixelSource_r = rgbSource[x+i][y+j][0];
		                int pixelSource_g = rgbSource[x+i][y+j][1];
		                int pixelSource_b = rgbSource[x+i][y+j][2];
		                int pixelPattern_r = rgbPattern[i][j][0];
		                int pixelPattern_g = rgbPattern[i][j][1];
		                int pixelPattern_b = rgbPattern[i][j][2];
		                
		                cur.delta += Math.sqrt(
		                				Math.abs(pixelSource_r - pixelPattern_r) * Math.abs(pixelSource_r - pixelPattern_r)
		                				+ Math.abs(pixelSource_g - pixelPattern_g) * Math.abs(pixelSource_g - pixelPattern_g)
		                				+ Math.abs(pixelSource_b - pixelPattern_b) * Math.abs(pixelSource_b - pixelPattern_b)
		                			);
		                
		                count++;
		                
		                /*if (cur.delta > result.delta) {
		                	i = rgbPattern.length;
		                	break;
		                }*/
		            }
		        }
		        cur.delta = cur.delta / (double) (count); 
		        
		        if (result == null || result.delta > cur.delta) { 
		        	result = cur;
		        }
		    }
		}
		
		return result;
	}
	
	
	private static int[][] moveWithXY(int[][] matrix, int X, int Y, int fillColour) {
		
		if (X == 0 && Y == 0) {
			throw new IllegalStateException("X=" + X + ", Y=" + Y);
		}
		
		int[][] result = matrix;
		
		if (X > 0) {
			if (Y > 0) {
				result = moveRightWithN(result, X, fillColour);
				result = moveDownWithN(result, Y, fillColour);
			} else {
				result = moveRightWithN(result, X, fillColour);
				result = moveUpWithN(result, Y, fillColour);
			}
		} else {
			if (Y > 0) {
				result = moveLeftWithN(result, X, fillColour);
				result = moveDownWithN(result, Y, fillColour);
			} else {
				result = moveLeftWithN(result, X, fillColour);
				result = moveUpWithN(result, Y, fillColour);
			}
		}
		
		return result;
	}
	
	
	private static int[][] moveLeftWithN(int[][] matrix, int N, int fillColour) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveLeftWith1(result, fillColour);
		}
		return result;
	}
	
	
	private static int[][] moveRightWithN(int[][] matrix, int N, int fillColour) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveRightWith1(result, fillColour);
		}
		return result;
	}
	
	
	private static int[][] moveUpWithN(int[][] matrix, int N, int fillColour) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveUpWith1(result, fillColour);
		}
		return result;
	}
	
	
	private static int[][] moveDownWithN(int[][] matrix, int N, int fillColour) {
		int[][] result = matrix;
		for (int i = 0; i < N; i++) {
			result = moveDownWith1(result, fillColour);
		}
		return result;
	}
	
	
	private static int[][] moveLeftWith1(int[][] matrix, int fillColour) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int first = matrix[0][i];
			for(int j = 1;j < matrix.length; j++) {
				result[j - 1][i] = matrix[j][i];
			}
			result[matrix.length-1][i] = (fillColour == -1) ? first : fillColour;
		}
		
		return result;
	}
	
	
	private static int[][] moveRightWith1(int[][] matrix, int fillColour) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int last = matrix[matrix.length - 1][i];
			for(int j = 0;j < matrix.length - 1; j++) {
				result[j + 1][i] = matrix[j][i];
			}
			result[0][i] = (fillColour == -1) ? last : fillColour;
		}
		
		return result;
	}
	
	
	private static int[][] moveUpWith1(int[][] matrix, int fillColour) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int first = matrix[i][0];
			for(int j = 1;j < matrix.length; j++) {
				result[i][j-1] = matrix[i][j];
			}
			result[i][matrix.length-1] = (fillColour == -1) ? first : fillColour;
		}
		
		return result;
	}
	
	
	private static int[][] moveDownWith1(int[][] matrix, int fillColour) {
		
		int[][] result = new int[matrix.length][matrix.length];
		
		for (int i = 0; i < matrix.length; i++) {
			int last = matrix[i][matrix.length - 1];
			for(int j = 0;j < matrix.length - 1; j++) {
				result[i][j+1] = matrix[i][j];
			}
			result[i][0] = (fillColour == -1) ? last : fillColour;
		}
		
		return result;
	}
	
	
	private static class Translation {
		
		
		int x;
		int y;
		
		
		Translation(int _x, int _y) {
			x = _x;
			y = _y;
		}
		
		
	    @Override
	    public int hashCode() {
	        return x + y;
	    }
	    
	    
	    @Override
	    public boolean equals(final Object obj) {
	        
	    	if (this == obj)
	            return true;
	        
	        if (getClass() != obj.getClass())
	            return false;
	        
	        final Translation other = (Translation) obj;
	        if (x == other.x && y == other.y) {
	        	return true;
	        }
	        
	        return false;
	    }
	    
	    
	    @Override
	    public String toString(){  
	    	return "[" + x + ", " + y + "]";  
	    }
	}
	
	
	public static final class PatternMatchingData {
		public int x;
		public int y;
		public int size;
		public int angle;
		public int color;
		public double delta;
		public int[][] pattern_gray;
		public int[][][] pattern_rgb;
	}
	
	
	public static void main(String[] args) {
		System.out.println(generateCirclePixels(2));
	}
}
