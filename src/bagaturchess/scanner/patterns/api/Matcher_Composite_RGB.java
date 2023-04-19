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

import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.machinelearning.classification.MatcherFinder_Base;
import bagaturchess.scanner.machinelearning.classification.MatcherFinder_RGB;


public class Matcher_Composite_RGB extends Matcher_Composite_Base {
	
	
	private MatcherFinder_Base matcherFinder;


	public Matcher_Composite_RGB(int imageSize, List<String> _netsNames, List<InputStream> _netsStreams, Map<String, Matcher_Base> _matchers) throws ClassNotFoundException, IOException {

		super(imageSize, _netsNames, _netsStreams, _matchers);

		matcherFinder = new MatcherFinder_RGB(imageSize / 8, netsStreams, netsNames);
	}
	
	
	@Override
	protected MatcherFinder_Base createMatcherFinder(int imageSize) throws ClassNotFoundException, IOException {
		return matcherFinder;
	}
	
	
	@Override
	protected Object normalizeMatrix(Object boardMatrix) throws IOException {
		int[][][] normalizedBoardMatrix = MatrixUtils.normalizeMatrix((int[][][])boardMatrix);
		//ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_normalized", "png", ScannerUtils.createRGBImage(normalizedBoardMatrix));
		return normalizedBoardMatrix;
	}
}
