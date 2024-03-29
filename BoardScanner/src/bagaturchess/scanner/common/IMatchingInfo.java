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
package bagaturchess.scanner.common;


public interface IMatchingInfo {
	public void setPhasesCount(int phasesCount);
	public void incCurrentPhase();
	public void setCurrentPhaseProgress(double progress); //Between [0 and 1]
	public void setPhaseName(String phaseName);
	public void setMatchingFinderInfo(String netName, double probability);
	public void setImageCroppingInfo();
	public void setSquare(int squareID);
	public boolean isSquareIDSet();
	public String getLatestMessage1();
	public String getLatestMessage2();
	public int getCurrentSquareID();
}
