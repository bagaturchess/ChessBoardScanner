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
import java.util.List;

//import bagaturchess.scanner.cnn.impl_dl4j.model.NetworkModel_Gray;
import bagaturchess.scanner.cnn.impl_deepnetts.model.NetworkModel_Gray;


public class MatcherFinder_Gray extends MatcherFinder_Base {
	
	
	public MatcherFinder_Gray(int squareSize, List<InputStream> netsStreams, List<String> _netsNames) throws ClassNotFoundException, IOException {
		super(squareSize, netsStreams, _netsNames);
	}
	
	
	protected ProbabilitiesCalculator createScanner(InputStream stream) throws ClassNotFoundException, IOException {
		return new ProbabilitiesCalculator_Gray(new NetworkModel_Gray(stream, squareSize));
	}
}
