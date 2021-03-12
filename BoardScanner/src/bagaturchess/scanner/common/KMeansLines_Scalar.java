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


public class KMeansLines_Scalar {
	
	
	public double[] centroids_values;
	public int[] centroids_ids;
	public int[] weights;
	
	
	public KMeansLines_Scalar(int K, double[] scalars) {
		
		//K-Means start
		int NUMBER_OF_CLUSTERS = K;
		
		//Initialize
		centroids_values = initCentroids(NUMBER_OF_CLUSTERS, scalars);
		
		centroids_ids = new int[scalars.length];
		
		for (int i = 0; i < scalars.length; i++) {
				
			double bestDistance = Double.MAX_VALUE;
			int bestCentroidID = -1;
			for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
				double distance = Math.abs(scalars[i] - centroids_values[centroid_id]);
				if (distance < bestDistance) {
					bestDistance = distance;
					bestCentroidID = centroid_id;
				}
			}
			
			centroids_ids[i] = bestCentroidID;
		}
		
		
		boolean hasGlobalChange = true;
		
		//Loop until convergence
		while (hasGlobalChange) {
			
			//System.out.println("start iteration " + count++);
			
			//Find avg
			double[] avgs_sum = new double[NUMBER_OF_CLUSTERS];
			double[] avgs_cnt = new double[NUMBER_OF_CLUSTERS];
			
			for (int i = 0; i < scalars.length; i++) {
				int centroid_id = centroids_ids[i];
				avgs_sum[centroid_id] += scalars[i];
				avgs_cnt[centroid_id]++;
			}
			
			for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
				if (avgs_cnt[centroid_id] != 0) {
					centroids_values[centroid_id] = avgs_sum[centroid_id] / (double) avgs_cnt[centroid_id];
				}
				//System.out.println("centroid_id " + centroid_id + " avg " + centroids_values[centroid_id]);
			}
			
			boolean hasChange = false;
			//Adjust values
			for (int i = 0; i < scalars.length; i++) {		
					
				double bestDistance = Double.MAX_VALUE;
				int bestCentroidID = -1;
				for (int centroid_id = 0; centroid_id < centroids_values.length; centroid_id++) {
					double distance = Math.abs(scalars[i] - centroids_values[centroid_id]);
					if (distance < bestDistance) {
						bestDistance = distance;
						bestCentroidID = centroid_id;
					}
				}
				
				if (bestCentroidID != centroids_ids[i]) {
					centroids_ids[i] = bestCentroidID;
					hasChange = true;
				}
			}
			
			hasGlobalChange = hasChange;
		}
		//K-Means end
		
		//Init weights
		weights = new int[NUMBER_OF_CLUSTERS];
		for (int i = 0; i < scalars.length; i++) {
			int cur_centroid_id = centroids_ids[i];
			weights[cur_centroid_id]++;
		}
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
	
	
	private double[] initCentroids(int count, double[] scalars) {
		
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < scalars.length; i++) {
			double value = scalars[i];
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}
		
		double[] centroids_values = new double[count];
		for (int i = 0; i < centroids_values.length; i++) {
			centroids_values[i] = min + (max - min) * (i + 1) / (double) centroids_values.length;
		}
		
		return centroids_values;
	}
}
