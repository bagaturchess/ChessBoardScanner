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
package bagaturchess.scanner.computervision.experiments;


import org.opencv.core.Point;

import bagaturchess.scanner.computervision.OpenCVUtils;


public class KMeansPoints {
	
	
	public Point[] centroids_values;
	public int[][] centroids_ids;
	public int[] weights;
	
	
	public KMeansPoints(int K, Point[][] points) {
		
		//K-Means start
		int NUMBER_OF_CLUSTERS = K;
		
		//Initialize between min and max
		centroids_values = initCentroids(NUMBER_OF_CLUSTERS, points);
		
		centroids_ids = new int[points.length][points.length];
		
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points.length; j++) {
				
				Point point = points[i][j];
				
				if (point == null) {
					continue;
				}
				
				double bestDistance = Double.MAX_VALUE;
				int bestCentroidID = -1;
				for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
					double distance = OpenCVUtils.distance(point, centroids_values[centroid_id]);
					if (distance < bestDistance) {
						bestDistance = distance;
						bestCentroidID = centroid_id;
					}
				}
				
				centroids_ids[i][j] = bestCentroidID;
			}
		}
		
		
		boolean hasGlobalChange = true;
		
		//Loop until convergence
		while (hasGlobalChange) {
			
			//System.out.println("start iteration " + count++);
			
			//Find avg
			double[] avgs_sum_x = new double[NUMBER_OF_CLUSTERS];
			long[] avgs_cnt_x = new long[NUMBER_OF_CLUSTERS];
			double[] avgs_sum_y = new double[NUMBER_OF_CLUSTERS];
			long[] avgs_cnt_y = new long[NUMBER_OF_CLUSTERS];
			
			for (int i = 0; i < points.length; i++) {
				for (int j = 0; j < points.length; j++) {
					
					Point point = points[i][j];
					
					if (point == null) {
						continue;
					}
					
					int centroid_id = centroids_ids[i][j];
					if (centroid_id != -1) { //-1 if point[i][j] is null
						avgs_sum_x[centroid_id] += point.x;
						avgs_cnt_x[centroid_id]++;
						avgs_sum_y[centroid_id] += point.y;
						avgs_cnt_y[centroid_id]++;
					}
				}
			}
			
			for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
				double new_x = avgs_cnt_x[centroid_id] == 0 ? centroids_values[centroid_id].x : avgs_sum_x[centroid_id] / (double) avgs_cnt_x[centroid_id];
				double new_y = avgs_cnt_y[centroid_id] == 0 ? centroids_values[centroid_id].y : avgs_sum_y[centroid_id] / (double) avgs_cnt_y[centroid_id];
				centroids_values[centroid_id] = new Point(new_x, new_y);
				System.out.println("centroid_id " + centroid_id + " avg " + centroids_values[centroid_id]);
			}
			
			boolean hasChange = false;
			//Adjust values
			for (int i = 0; i < points.length; i++) {
				for (int j = 0; j < points.length; j++) {		
					
					Point point = points[i][j];
					
					if (point == null) {
						continue;
					}
					
					double bestDistance = Double.MAX_VALUE;
					int bestCentroidID = -1;
					for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
						double distance = OpenCVUtils.distance(point, centroids_values[centroid_id]);
						if (distance < bestDistance) {
							bestDistance = distance;
							bestCentroidID = centroid_id;
						}
					}
					
					if (bestCentroidID != centroids_ids[i][j]) {
						centroids_ids[i][j] = bestCentroidID;
						hasChange = true;
					}
				}
			}
			
			hasGlobalChange = hasChange;
		}
		//K-Means end
		
		//Init weights
		weights = new int[NUMBER_OF_CLUSTERS];
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points.length; j++) {
				int cur_centroid_id = centroids_ids[i][j];
				if (cur_centroid_id != -1) {
					weights[cur_centroid_id]++;
				}
			}
		}
	}
	
	
	public int getFarthestCentroidIndex(Point center) {
		
		double maxDistance = Double.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < centroids_values.length; i++) {
			double distance = OpenCVUtils.distance(center, centroids_values[i]);
			if (distance > maxDistance) {
				maxDistance = distance;
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	
	
	public int getMaxWeightIndex() {
		int index = 0;
		int maxValue = 0;
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > maxValue) {
				maxValue = weights[i];
				index = i;
			}
		}
		return index;
	}
	
	
	public int getMaxWeight() {
		int maxValue = 0;
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > maxValue) {
				maxValue = weights[i];
			}
		}
		return maxValue;
	}
	
	
	public int[] get2MaxWeightsIndexes() {
		int[] result_indexes = new int[2];
		int[] result_values = new int[2];
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] > result_values[0]) {
				result_values[0] = weights[i];
				result_indexes[0] = i;
			}
		}
		for (int i = 0; i < weights.length; i++) {
			if (weights[i] == result_values[0]) {
				continue;
			}
			if (weights[i] > result_values[1]) {
				result_values[1] = weights[i];
				result_indexes[1] = i;
			}
		}
		return result_indexes;
	}
	
	
	private Point[] initCentroids(int count, Point[][] points) {
		
		double min_x = Double.MAX_VALUE;
		double max_x = Double.MIN_VALUE;
		double min_y = Double.MAX_VALUE;
		double max_y = Double.MIN_VALUE;
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points[0].length; j++) {
				Point point = points[i][j];
				if (point == null) {
					continue;
				}
				if (point.x < min_x) {
					min_x = point.x;
				}
				if (point.x > max_x) {
					max_x = point.x;
				}
				if (point.y < min_y) {
					min_y = point.y;
				}
				if (point.y > max_y) {
					max_y = point.y;
				}
			}
		}
		
		Point[] centroids_values = new Point[count];
		for (int i = 0; i < centroids_values.length; i++) {
			centroids_values[i] = new Point(min_x + (max_x - min_x) * (i + 1) / (double) centroids_values.length, min_y + (max_y - min_y) * (i + 1) / (double) centroids_values.length);
		}
		
		return centroids_values;
	}
}
