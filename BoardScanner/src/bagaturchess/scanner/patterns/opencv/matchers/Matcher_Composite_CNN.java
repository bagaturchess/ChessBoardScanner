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
package bagaturchess.scanner.patterns.opencv.matchers;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.scanner.cnn.compute.MatcherFinder;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public class Matcher_Composite_CNN extends Matcher_Base {
	
	private List<String> netsNames;
	private List<InputStream> netsStreams;
	private Map<String, String> netToSetMappings;
	
	private Map<String, Matcher_Base> matchers;
	private MatcherFinder finder;
	
	
	public Matcher_Composite_CNN(int imageSize, List<String> _netsNames, List<InputStream> _netsStreams, Map<String, String> _netToSetMappings) throws ClassNotFoundException, IOException {
		
		super(null);
		
		netsNames = _netsNames;
		netsStreams = _netsStreams;
		netToSetMappings = _netToSetMappings;
		
		Matcher_Base matcherSet1 = new Matcher_Set1(imageSize);
		Matcher_Base matcherSet2 = new Matcher_Set2(imageSize);
		
		matchers = new HashMap<String, Matcher_Base>();
		matchers.put(matcherSet1.getPiecesSetName(), matcherSet1);
		matchers.put(matcherSet2.getPiecesSetName(), matcherSet2);
		
		finder = new MatcherFinder(imageSize / 8, netsStreams, netsNames);
	}
	
	
	@Override
	public ResultPair<String, MatchingStatistics> scan(int[][] grayBoard, IMatchingInfo matchingInfo) throws IOException {
		
		if (matchingInfo != null) matchingInfo.setPhasesCount(2);
		

		if (matchingInfo != null) matchingInfo.setCurrentPhase(1);
		String cnn_name = finder.findMatcher(grayBoard, matchingInfo);
		String piecesSetName = netToSetMappings.get(cnn_name);
		Matcher_Base matcher = matchers.get(piecesSetName);
		if (matcher == null) {
			throw new IllegalStateException("Matcher for pieces set " + piecesSetName + " not found.");
		}
		
		
		if (matchingInfo != null) matchingInfo.setCurrentPhase(2);
		System.out.println("Matcher_Composite: scan: Selected matcher is " + matcher.getClass().getCanonicalName());
		ResultPair<String, MatchingStatistics> result = matcher.scan(grayBoard, matchingInfo);
		
		
		//if (matchingInfo != null) matchingInfo.setCurrentPhase(3);
		//System.out.println("Matcher_Composite: scan: Selected matcher is " + matcher.getClass().getCanonicalName() + " with emptySquareThreshold = " + result.getThird());
		//result = matcher.scan(grayBoard, matchingInfo, result.getThird());
		
		
		return result;
	}
}
