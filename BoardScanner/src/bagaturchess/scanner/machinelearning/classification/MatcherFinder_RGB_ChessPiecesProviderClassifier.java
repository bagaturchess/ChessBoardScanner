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
import bagaturchess.scanner.machinelearning.classification.probabilities.ProbabilitiesCalculator_RGB_ChessPiecesProviderClassifier;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;


public class MatcherFinder_RGB_ChessPiecesProviderClassifier extends MatcherFinder_Base {
	
	
	private ProbabilitiesCalculator piecesProviderClassifier;
	
	
	public MatcherFinder_RGB_ChessPiecesProviderClassifier(int squareSize, InputStream _piecesProviderClassifierNet, List<String> _netsNames) throws ClassNotFoundException, IOException {
		
		super(squareSize, new ArrayList<InputStream>(), _netsNames);
		
		piecesProviderClassifier = new ProbabilitiesCalculator_RGB_ChessPiecesProviderClassifier(ProviderSwitch.getInstance().create(3, _piecesProviderClassifierNet, squareSize));
		
		//System.out.println("MatcherFinder_RGB_ChessPiecesProviderClassifier: " + piecesProviderClassifier);
	}
	
	
	protected ProbabilitiesCalculator createScanner(InputStream stream) throws ClassNotFoundException, IOException {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public String findMatcher(Object image, IMatchingInfo matchingInfo) {
		
		long startTime = System.currentTimeMillis();
		
		double[] probs = piecesProviderClassifier.getAccumulatedProbabilitiesByLabelIndex(image, matchingInfo);
		
		System.out.println("MatcherFinder_Base: probs size is " + probs.length);
		
		String bestName = null;
		double bestProb = 0;
		for (int i = 0; i < probs.length; i++) {
			//When testing it is possible to have only a few of the nets available.
			if (i < netsNames.size()) {
				double currentProb = probs[i];
				String currentName = netsNames.get(i);
				if (currentProb >= bestProb) {
					bestProb = currentProb;
					bestName = currentName;
				}
				System.out.println("MatcherFinder_Base: " + currentName + " " + currentProb);
			}
		}
		
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("MatcherFinder_Base: Time is " + (endTime - startTime) + " ms, best is " + bestName);
		
		return bestName;
	}
}
