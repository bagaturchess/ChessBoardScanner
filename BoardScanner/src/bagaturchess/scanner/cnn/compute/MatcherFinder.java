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
package bagaturchess.scanner.cnn.compute;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.cnn.impl_dl4j.model.NetworkModel_Gray;
//import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_Gray;
import bagaturchess.scanner.common.IMatchingInfo;


public class MatcherFinder {
	
	
	private List<ProbabilitiesCalculator> scanners;
	private List<String> netsNames;
	
	
	public MatcherFinder(int squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		
		scanners = new ArrayList<ProbabilitiesCalculator>();
		netsNames = _netsNames;
		
		for (int i = 0; i < netsStreams.size(); i++) {
			scanners.add(new ProbabilitiesCalculator_Gray(new NetworkModel_Gray(netsStreams.get(i), squareSize)));
		}
	}
	
	
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
			System.out.println("MatcherFinder: " + currentName + " " + currentProb);
		}
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("MatcherFinder: Time is " + (endTime - startTime) + " ms, best is " + bestName);
		
		return bestName;
	}
}
