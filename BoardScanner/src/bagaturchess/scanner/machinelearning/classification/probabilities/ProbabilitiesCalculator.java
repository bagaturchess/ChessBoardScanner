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
package bagaturchess.scanner.machinelearning.classification.probabilities;


import java.io.IOException;

import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.machinelearning.model.NetworkModel;


public abstract class ProbabilitiesCalculator {
	
	
	protected NetworkModel networkModel;
	protected Object network;
	
	
	public ProbabilitiesCalculator(NetworkModel _networkModel) throws ClassNotFoundException, IOException {
		networkModel = _networkModel;
		network = networkModel.getNetwork();
	}
	
	
	public abstract double getAccumulatedProbability(Object image, IMatchingInfo matchingInfo, VarStatistic varStatistic);
	
	
	public double[] getAccumulatedProbabilitiesByLabelIndex(Object image, IMatchingInfo matchingInfo, VarStatistic stats) {
		double[] result = new double[1];
		result[0] = getAccumulatedProbability(image, matchingInfo, stats);
		return result;
	}
}
