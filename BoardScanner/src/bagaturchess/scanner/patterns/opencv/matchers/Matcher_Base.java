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

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.patterns.api.MatchingStatistics;


public abstract class Matcher_Base {
	
	
	protected BoardProperties boardProperties;
	private String displayName;


	public Matcher_Base(BoardProperties _imageProperties, String _displayName) {
		boardProperties = _imageProperties;
		displayName = _displayName;
	}
	
	
	public abstract ResultPair<String, MatchingStatistics> scan(Object boardMatrix, IMatchingInfo matchingInfo) throws IOException;
	
	
	public String getPiecesSetName() {
		return boardProperties.getPiecesSetFileNamePrefix();
	}
	
	
	protected String getDisplayName() {
		return displayName;
	}
}
