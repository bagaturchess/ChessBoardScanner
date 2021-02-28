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
package bagaturchess.scanner.cnn.impl_dn.learning;


import java.util.ArrayList;
import java.util.List;


public class DataSetInitPair {

	
	protected List<Object> images;
	protected List<Integer> pids;
	
	
	DataSetInitPair() {
		images = new ArrayList<Object>();
		pids = new ArrayList<Integer>();
	}
	
	
	public List<Object> getImages() {
		return images;
	}
	
	
	public List<Integer> getPIDs() {
		return pids;
	}
}
