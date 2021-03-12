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
package bagaturchess.scanner.patterns;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.MatchingInfo_BaseImpl;
import bagaturchess.scanner.common.ResultPair;
//import bagaturchess.scanner.patterns.impl1.matchers.*;
import bagaturchess.scanner.patterns.opencv.matchers.*;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.MatchingStatistics;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Rotate;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_WhiteBackground;
import bagaturchess.scanner.patterns.opencv.preprocess.ImagePreProcessor_OpenCV;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Base;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Crop;
import bagaturchess.scanner.patterns.impl1.preprocess.ImagePreProcessor_Crop_KMeans;


public class AllMain {
	
	
	public static void main(String[] args) {
		
		try {
			
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
			Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/preprocess/test35.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/test3.jpg");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/lichess.org/test2.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/chess.com/test1.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/cnn/chess.com/set1/pictures/test7.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/cnn/lichess.org/set1/pictures/test7.png");
			
			
			//Preprocess image
			BoardProperties boardProperties_processor = new BoardProperties(512);
			ImagePreProcessor_Base processor_crop_kmeans = new ImagePreProcessor_Crop_KMeans(boardProperties_processor);
			ImagePreProcessor_Base processor_crop = new ImagePreProcessor_Crop(boardProperties_processor);
			ImagePreProcessor_Base processor_rotate = new ImagePreProcessor_Rotate(boardProperties_processor);
			ImagePreProcessor_Base processor_opencv = new ImagePreProcessor_OpenCV(boardProperties_processor);
			ImagePreProcessor_Base processor_whitebg = new ImagePreProcessor_WhiteBackground(boardProperties_processor);
			
			long startTime = System.currentTimeMillis();
			//Object preProcessedImage = processor_whitebg.filter(image);
			//Object preProcessedImage = processor_crop_kmeans.filter(image);
			Object preProcessedImage = processor_opencv.filter(image);
			//Object preProcessedImage = processor_crop_kmeans.filter(image);
			//preProcessedImage = processor_crop.filter(preProcessedImage);
			//preProcessedImage = processor_rotate.filter(preProcessedImage);
			System.out.println("Filtered in " + (System.currentTimeMillis() - startTime) + "ms");
			
			
			BoardProperties boardProperties_matcher = new BoardProperties(256);
			preProcessedImage = ImageHandlerSingleton.getInstance().resizeImage(preProcessedImage, boardProperties_matcher.getImageSize());
			int[][] grayBoard = ImageHandlerSingleton.getInstance().convertToGrayMatrix(preProcessedImage);
			
			
			//Create composite matcher
			List<String> netsNames = new ArrayList<String>();
			netsNames.add("cnn_lichessorg1.net");
			netsNames.add("cnn_chesscom1.net");

			Map<String, String> netToSetMappings = new HashMap<String, String>();
			netToSetMappings.put("cnn_lichessorg1.net", "set1");
			netToSetMappings.put("cnn_chesscom1.net", "set2");
			
			List<InputStream> netsStreams = new ArrayList<InputStream>();
			for (int i = 0; i < netsNames.size(); i++) {
				netsStreams.add(new FileInputStream(netsNames.get(i)));
			}
			
			Matcher_Base matcher = new Matcher_Composite_CNN(boardProperties_matcher.getImageSize(), netsNames, netsStreams, netToSetMappings);
			
			
			//Start matching
			IMatchingInfo matchingInfo = new MatchingInfo_BaseImpl();
			startTime = System.currentTimeMillis();
			ResultPair<String, MatchingStatistics> result = matcher.scan(grayBoard, matchingInfo);
            System.out.println(result.getFirst() + " " + result.getSecond().totalDelta + " " + (System.currentTimeMillis() - startTime) + "ms");
            
            System.exit(0);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
