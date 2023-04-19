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


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import bagaturchess.scanner.machinelearning.classification.MatcherFinder_Base;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.ResultPair;


public abstract class Matcher_Composite_Base extends Matcher_Base {
	
	
	private int imageSize;
	protected List<String> netsNames;
	protected List<InputStream> netsStreams;
	
	private Map<String, Matcher_Base> matchers;
	
	
	public Matcher_Composite_Base(int _imageSize, List<String> _netsNames, List<InputStream> _netsStreams, Map<String, Matcher_Base> _matchers) throws ClassNotFoundException, IOException {
		
		super(null, "Matcher_Composite_Gray");
		
		imageSize = _imageSize;
		netsNames = _netsNames;
		netsStreams = _netsStreams;
		matchers = _matchers;
	}


	protected abstract MatcherFinder_Base createMatcherFinder(int imageSize) throws ClassNotFoundException, IOException;
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) throws IOException, ClassNotFoundException {
		
		//if (matchingInfo != null) matchingInfo.setPhasesCount(2);

		if (matchingInfo != null) matchingInfo.incCurrentPhase();
		
		
		Object normalizedBoardMatrix = normalizeMatrix(boardMatrix);
		
		MatcherFinder_Base finder = createMatcherFinder(imageSize);
		String cnn_name = finder.findMatcher(normalizedBoardMatrix, matchingInfo);
		
		
		Matcher_Base matcher = matchers.get(cnn_name);
		
		if (matcher == null) {
			throw new IllegalStateException("Matcher " + cnn_name + " not found.");
		}
		
		if (matchingInfo != null) matchingInfo.incCurrentPhase();
		System.out.println("Matcher_Composite: scan: Selected matcher is " + matcher.getClass().getCanonicalName());
		ResultPair<String, MatchingStatistics> result = matcher.scan(normalizedBoardMatrix, matchingInfo);
		
		
		//if (matchingInfo != null) matchingInfo.setCurrentPhase(3);
		//System.out.println("Matcher_Composite: scan: Selected matcher is " + matcher.getClass().getCanonicalName() + " with emptySquareThreshold = " + result.getThird());
		//result = matcher.scan(grayBoard, matchingInfo, result.getThird());
		
		
		return result;
	}
	
	
	protected abstract Object normalizeMatrix(Object boardMatrix) throws IOException;
}
