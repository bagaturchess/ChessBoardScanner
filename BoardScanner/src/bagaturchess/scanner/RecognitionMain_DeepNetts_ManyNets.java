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
package bagaturchess.scanner;


import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.MatchingInfo_BaseImpl;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.computervision.preprocessing.ImagePreProcessor_Base;
import bagaturchess.scanner.computervision.preprocessing.ImagePreProcessor_OpenCV;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.Matcher_Base;
import bagaturchess.scanner.patterns.api.Matcher_Composite_Gray;
import bagaturchess.scanner.patterns.api.Matcher_Composite_RGB;
import bagaturchess.scanner.patterns.api.MatchingStatistics;
import bagaturchess.scanner.patterns.cnn.matchers.*;
import bagaturchess.scanner.utils.ScannerUtils;


public class RecognitionMain_DeepNetts_ManyNets {
	
	
	public static void main(String[] args) {
		
		try {
			
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/preprocess/test38.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/test3.jpg");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/lichess.org/test2.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/chess.com/test1.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./res/cnn/chess.com/set1/pictures/test7.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./res/legendary_games/demo1.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/lichess.org/input1.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/chess24.com/input1.png");
			//Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/chess.com/test1.png");
			Object image = ImageHandlerSingleton.getInstance().loadImageFromFS("./data/tests/books/input2.png");
			
			
			long startTime = System.currentTimeMillis();
			
			
			//Preprocessing image
			BoardProperties boardProperties_processor = new BoardProperties(512);
			
			image = ImageHandlerSingleton.getInstance().resizeImage(image, boardProperties_processor.getImageSize());
			
			ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_input", "png", image);
			
			Object forMatching = image;
			
			/*
			ImagePreProcessor_Base processor_opencv = new ImagePreProcessor_OpenCV(boardProperties_processor);
			
			MatOfPoint2f boardCorners = processor_opencv.filter(image);
			
			if (boardCorners != null) {
				
				Mat extractedBoard = (Mat) processor_opencv.extractBoard(image, boardCorners);
				
				forMatching = (BufferedImage) ImageHandlerSingleton.getInstance().mat2Graphic(extractedBoard);
				ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_croped", "png", forMatching);
				
				System.out.println("Chess board extracted in " + (System.currentTimeMillis() - startTime) + "ms");
			} else {
				System.out.println("Cannot extract chess board from image and will use the original image.");
			}*/
			
			
			ProviderSwitch.MLFrameworkName = "deepnetts";
			
			
			BoardProperties matcherBoardProperties = new BoardProperties(256);
			Object cropedProcessedImage = ImageHandlerSingleton.getInstance().resizeImage(forMatching, matcherBoardProperties.getImageSize());
			
			
			//Create composite matcher
			
			
            List<String> netsNames = new ArrayList<String>();
            netsNames.add("cnn_chesscom_set_1.dnet");
            netsNames.add("cnn_lichessorg_set_1.dnet");
            netsNames.add("cnn_chess24com_set_1.dnet");
            netsNames.add("cnn_books_set_1.dnet");
            
			List<InputStream> netsStreams = new ArrayList<InputStream>();
			for (int i = 0; i < netsNames.size(); i++) {
				netsStreams.add(new FileInputStream(netsNames.get(i)));
			}
			
            Map<String, Matcher_Base> matchers = new HashMap<String, Matcher_Base>();
            matchers.put("cnn_chesscom_set_1.dnet", new Matcher_RGB(new BoardProperties(matcherBoardProperties.getImageSize(), "set2"), "cnn_chesscom_set_1.dnet"));
            matchers.put("cnn_lichessorg_set_1.dnet", new Matcher_RGB(new BoardProperties(matcherBoardProperties.getImageSize(), "set2"), "cnn_lichessorg_set_1.dnet"));
            matchers.put("cnn_chess24com_set_1.dnet", new Matcher_RGB(new BoardProperties(matcherBoardProperties.getImageSize(), "set3"), "cnn_chess24com_set_1.dnet"));
            matchers.put("cnn_books_set_1.dnet", new Matcher_RGB(new BoardProperties(matcherBoardProperties.getImageSize(), "set2"), "cnn_books_set_1.dnet"));
			
            Matcher_Base matcher = new Matcher_Composite_RGB(matcherBoardProperties.getImageSize(), netsNames, netsStreams, matchers);
			
			
			//Start matching
			IMatchingInfo matchingInfo = new MatchingInfo_BaseImpl();
			startTime = System.currentTimeMillis();
			
			int[][][] rgbBoard = ScannerUtils.convertToRGBMatrix((BufferedImage) cropedProcessedImage);
			
			ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_" + matcherBoardProperties.getImageSize(), "png", ScannerUtils.createRGBImage(rgbBoard));
			
			ResultPair<String, MatchingStatistics> result = matcher.scan(rgbBoard, matchingInfo);
            System.out.println(result.getFirst() + " " + result.getSecond().totalDelta + " " + (System.currentTimeMillis() - startTime) + "ms");
			
			
            System.exit(0);
            
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
