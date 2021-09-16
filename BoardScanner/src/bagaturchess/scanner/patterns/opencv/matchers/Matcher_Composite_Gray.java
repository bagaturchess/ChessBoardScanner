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
import java.util.List;
import java.util.Map;

import bagaturchess.scanner.cnn.compute.MatcherFinder_Base;
import bagaturchess.scanner.cnn.compute.MatcherFinder_Gray;


public class Matcher_Composite_Gray extends Matcher_Composite_Base {
	
	
	
	public Matcher_Composite_Gray(int imageSize, List<String> _netsNames, List<InputStream> _netsStreams, Map<String, Matcher_Base> _matchers) throws ClassNotFoundException, IOException {
		super(imageSize, _netsNames, _netsStreams, _matchers);
		
	}
	
	
	@Override
	protected MatcherFinder_Base createMatcherFinder(int imageSize) throws ClassNotFoundException, IOException {
		return new MatcherFinder_Gray(imageSize / 8, netsStreams, netsNames);
	}
}
