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
package com.jars.shrinker;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import bagaturchess.scanner.cnn.scan.MatcherFinder;
import bagaturchess.scanner.cnn.utils.ScannerUtils;
import bagaturchess.scanner.common.BoardProperties;


public class ScannerClassesExtractor {
	
	
	public static void main(String[] args) {
		
		try {
			
			BoardProperties boardProperties = new BoardProperties(256);
			
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/lichess.org/test8.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/chess.com/test5.png"));
			//BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/lichess.org/set1/input1.png"));
			BufferedImage boardImage = ImageIO.read(new File("./data/tests/cnn/chess.com/set1/input1.png"));
			boardImage = ScannerUtils.resizeImage(boardImage, boardProperties.getImageSize());
			int[][] boardMatrix = ScannerUtils.convertToGrayMatrix(boardImage);
			
			
			List<String> netsNames = new ArrayList<String>();
			netsNames.add("scanner.lichessorg1.bin");
			netsNames.add("scanner.chesscom1.bin");
			
			List<InputStream> netsStreams = new ArrayList<InputStream>();
			for (int i = 0; i < netsNames.size(); i++) {
				netsStreams.add(new FileInputStream(netsNames.get(i)));
			}
			
			MatcherFinder finder = new MatcherFinder(boardProperties.getSquareSize(), netsStreams, netsNames);
			finder.findMatcher(boardMatrix);
			
			
			//Shrink libraries
			//Set<Class> classes = ClassLoaderUtils.getLoadedClasses(Thread.currentThread().getContextClassLoader());
			Class[] allClasses = JavaAgent.getInstrumentation().getAllLoadedClasses();
			Set<Class> classes = ClassLoaderUtils.enrichWithParents(allClasses);
			JarsManager jarsManager = new JarsManager("./../BoardScanner/libs/dl4j-1.0.0-beta3-short", "./libs/dl4j-1.0.0-beta3-shrinked.jar");
			jarsManager.extractClasses(classes);
			jarsManager.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
