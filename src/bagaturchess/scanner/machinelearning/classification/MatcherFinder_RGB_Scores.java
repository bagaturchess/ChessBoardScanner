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

import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.machinelearning.classification.probabilities.ProbabilitiesCalculator;
import bagaturchess.scanner.machinelearning.classification.probabilities.ProbabilitiesCalculator_Scores_RGB;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;


public class MatcherFinder_RGB_Scores extends MatcherFinder_Base {
	
	
	public MatcherFinder_RGB_Scores(int squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		super(squareSize, netsStreams, _netsNames);
	}
	
	
	protected ProbabilitiesCalculator createScanner(InputStream stream) throws ClassNotFoundException, IOException {
		return new ProbabilitiesCalculator_Scores_RGB(ProviderSwitch.getInstance().create(3, stream, squareSize));
	}
	
	
	@Override
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
		
		
		
		String bestName = null;
		
		double bestProb = 0;
		
		for (int i = 0; i < scanners.size(); i++) {
			
			String currentName = netsNames.get(i);
			
			double currentProb = probs.get(i);
			
			if (currentProb == Double.NaN) {
				
				throw new IllegalStateException("currentProb=" + currentProb);
			}
			
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
