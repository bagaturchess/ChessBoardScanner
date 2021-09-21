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

import bagaturchess.scanner.machinelearning.classification.probabilities.ProbabilitiesCalculator;
import bagaturchess.scanner.common.IMatchingInfo;


public abstract class MatcherFinder_Base {
	
	
	protected List<ProbabilitiesCalculator> scanners;
	protected List<String> netsNames;
	
	protected int squareSize;
	
	
	public MatcherFinder_Base(int _squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		
		squareSize = _squareSize;
		netsNames = _netsNames;
		
		scanners = new ArrayList<ProbabilitiesCalculator>();
		for (int i = 0; i < netsStreams.size(); i++) {
			scanners.add(createScanner(netsStreams.get(i)));
		}
	}
	
	
	protected abstract ProbabilitiesCalculator createScanner(InputStream stream) throws ClassNotFoundException, IOException;
	
	
	public String findMatcher(Object image, IMatchingInfo matchingInfo) {
		
		long startTime = System.currentTimeMillis();
		
		String bestName = null;
		double bestProb = 0;
		for (int i = 0; i < scanners.size(); i++) {
			String currentName = netsNames.get(i);
			double currentProb = scanners.get(i).getAccumulatedProbability(image);
			if (currentProb >= bestProb) {
				bestProb = currentProb;
				bestName = currentName;
			}
			if (matchingInfo != null) {
				matchingInfo.setCurrentPhaseProgress(i / (double) scanners.size());
				matchingInfo.setMatchingFinderInfo(currentName, currentProb);
			}
			System.out.println("MatcherFinder_Base: " + currentName + " " + currentProb);
		}
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("MatcherFinder_Base: Time is " + (endTime - startTime) + " ms, best is " + bestName);
		
		return bestName;
	}
}
