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

package bagaturchess.scanner.cnn.learning.impl_dl4j;


import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


public class DataSetIterator_Scanner implements DataSetIterator {
	
	
	private static final long serialVersionUID = -3010080072504907088L;
	
	
	private List<DataSet> entries;
	private int current = 0;
	private DataSetPreProcessor dataSetPreProcessor;
	
	
	public DataSetIterator_Scanner() {
		entries = new ArrayList<DataSet>();
		reset();
	}
	
	
	public void addEntry(DataSet entry) {
		entries.add(entry);
	}
	
	
	@Override
	public boolean hasNext() {
		return current < entries.size();
	}

	@Override
	public DataSet next() {
		return entries.get(current++);
	}

	@Override
	public boolean asyncSupported() {
		return false;
	}

	@Override
	public int batch() {
		return entries.size();
	}

	@Override
	public DataSetPreProcessor getPreProcessor() {
		return dataSetPreProcessor;
	}

	@Override
	public void reset() {
		current = 0;
	}

	@Override
	public boolean resetSupported() {
		return true;
	}

	@Override
	public void setPreProcessor(DataSetPreProcessor _dataSetPreProcessor) {
		dataSetPreProcessor = _dataSetPreProcessor;
	}
	
	@Override
	public int inputColumns() {
		throw new UnsupportedOperationException();
	}

	@Override
	public DataSet next(int arg0) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<String> getLabels() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int totalOutcomes() {
		throw new UnsupportedOperationException();
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
