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
package bagaturchess.scanner.machinelearning.classification;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.machinelearning.classification.probabilities.ProbabilitiesCalculator;
import bagaturchess.scanner.common.IMatchingInfo;


public abstract class MatcherFinder_Base {
	
	
	protected List<ProbabilitiesCalculator> scanners;
	protected List<String> netsNames;
	protected List<VarStatistic> scanners_stats;
	
	protected int squareSize;
	
	
	public MatcherFinder_Base(int _squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		
		squareSize = _squareSize;
		netsNames = _netsNames;
		
		scanners = new ArrayList<ProbabilitiesCalculator>();
		for (int i = 0; i < netsStreams.size(); i++) {
			scanners.add(createScanner(netsStreams.get(i)));
		}
		
		scanners_stats = new ArrayList<VarStatistic>();
		for (int i = 0; i < netsStreams.size(); i++) {
			scanners_stats.add(new VarStatistic(false));
		}
	}
	
	
	protected abstract ProbabilitiesCalculator createScanner(InputStream stream) throws ClassNotFoundException, IOException;
	
	
	public String findMatcher(Object image, IMatchingInfo matchingInfo) {
		
		long startTime = System.currentTimeMillis();
		
		List<Double> probs = new ArrayList<Double>();
		
		for (int i = 0; i < scanners.size(); i++) {
			
			String currentName = netsNames.get(i);
			
			double currentProb = scanners.get(i).getAccumulatedProbability(image, matchingInfo, scanners_stats.get(i));
			
			probs.add(currentProb);
			
			if (matchingInfo != null) {
				
				matchingInfo.setCurrentPhaseProgress(i / (double) scanners.size());
				matchingInfo.setMatchingFinderInfo(currentName, currentProb);
			}
			
			System.out.println("MatcherFinder_Base(PROB): " + currentName + " " + currentProb + " " + scanners_stats.get(i));
		}
		
		
		double max_amount = Double.MIN_VALUE;
		
		double max_mean = 0;
		
		for (int i = 0; i < scanners_stats.size(); i++) {
			
			VarStatistic stats = scanners_stats.get(i);
			
			double amount = stats.getTotalAmount();
			
			if (amount == Double.NaN) {
				
				throw new IllegalStateException("amount=" + amount);
			}
			
			if (max_amount < amount) {
				
				max_amount = amount;
			}
			
			double meam = stats.getEntropy();
			
			if (meam == Double.NaN) {
				
				throw new IllegalStateException("meam=" + meam);
			}
			
			if (max_mean < meam) {
				
				max_mean = meam;
			}
		}
		
		
		String bestName = null;
		
		double bestProb = 0;
		
		for (int i = 0; i < scanners.size(); i++) {
			
			String currentName = netsNames.get(i);
			
			double currentProb = probs.get(i);
			
			if (currentProb == Double.NaN) {
				
				throw new IllegalStateException("currentProb=" + currentProb);
			}
			
			//currentProb += (max_mean - scanners_stats.get(i).getEntropy());
			
			//currentProb /= max_amount / scanners_stats.get(i).getTotalAmount();
		
			if (currentProb > 1) {
				
				currentProb = 1;
			}
			
			if (currentProb < 0) {
				
				currentProb = 0;
			}
			
			if (currentProb >= bestProb) {
				
				bestProb = currentProb;
				
				bestName = currentName;
			}
			
			System.out.println("MatcherFinder_Base(SELECT): " + currentName + " " + currentProb + " " + scanners_stats.get(i));
		}
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("MatcherFinder_Base: Time is " + (endTime - startTime) + " ms, best is " + bestName);
		
		return bestName;
	}
}
