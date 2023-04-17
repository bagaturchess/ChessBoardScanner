package bagaturchess.scanner.tests;


import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.common.IMatchingInfo;
import bagaturchess.scanner.common.MatchingInfo_BaseImpl;
import bagaturchess.scanner.common.MatrixUtils;
import bagaturchess.scanner.common.ResultPair;
import bagaturchess.scanner.machinelearning.SupervisedData;
import bagaturchess.scanner.machinelearning.model.ProviderSwitch;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;
import bagaturchess.scanner.patterns.api.Matcher_Base;
import bagaturchess.scanner.patterns.api.Matcher_Composite_RGB;
import bagaturchess.scanner.patterns.api.MatchingStatistics;
import bagaturchess.scanner.patterns.cnn.matchers.Matcher_RGB;
import bagaturchess.scanner.utils.ScannerUtils;


public class Test {

	
	public static final String INITIAL_FEN			 	= "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	
	
	public static void main(String[] args) {
		
		
		try {
			
			
			ProviderSwitch.MLFrameworkName = "deepnetts";
			
			
			BoardProperties matcherBoardProperties = new BoardProperties(256);
			
			
			//List all NNs
            List<String> netsNames = new ArrayList<String>();
            
            netsNames.add("dnet_books_set_1_extended.dnet");
            netsNames.add("dnet_books_set_2_extended.dnet");
            netsNames.add("dnet_books_set_3_extended.dnet");
            netsNames.add("dnet_chesscom_set_1_extended.dnet");
            netsNames.add("dnet_chesscom_set_2_extended.dnet");
            netsNames.add("dnet_chess24com_set_1_extended.dnet");
            netsNames.add("dnet_lichessorg_set_1_extended.dnet");
            netsNames.add("dnet_universal_extended.dnet");
            
            
            /*netsNames.add("dnet_books_set_1.dnet");
            netsNames.add("dnet_chesscom_set_1.dnet");
            netsNames.add("dnet_chess24com_set_1.dnet");
            netsNames.add("dnet_lichessorg_set_1.dnet");
            */
            
            //Create NNs variations
            List<List<String>> nets_variations = new ArrayList<List<String>>();
            
            for (int i = 0; i < netsNames.size(); i++) {
            	
                nets_variations.add(Arrays.asList(new String[] {netsNames.get(i)}));
            }
            
            nets_variations.add(netsNames);
            
            
            //Run the tests
			SupervisedData[] all_sets = SupervisedData.source_set_all;
			//SupervisedData[] all_sets = SupervisedData.test_set_all;
			
            String text_result = "";
            
            for (int variation = 0; variation < nets_variations.size(); variation++) {
            	
            	List<String> net_names = nets_variations.get(variation);
            	
                Matcher_Base matcher = new Matcher_Composite_RGB(
                		matcherBoardProperties.getImageSize(),
                		net_names,
                		createStreams(net_names),
                		createMatchers(matcherBoardProperties, net_names)
                	);
    					;
    			long startTime = System.currentTimeMillis();
    			
    			int count_all 			= 0;
    			
                int count_successful 	= 0;
                
    			for (int i = 0; i < all_sets.length; i++) {
    				
    				SupervisedData cur_source_set = all_sets[i];
    				
    				String[] files = cur_source_set.input_files;
    				String[] fens = cur_source_set.fens;
    				
    				for (int j = 0; j < files.length; j++) {
    					
    					count_all++;
    					
    					System.out.println(count_all + " " + files[j] + " == " + fens[j]);
    					
    					Object image 	= ImageHandlerSingleton.getInstance().loadImageFromFS(files[j]);
    					String fen 		= fens[j];
    					
    					int index_if_fen_is_complete = fen.indexOf(" ");
    					if (index_if_fen_is_complete != -1) {
    						fen = fen.substring(0, index_if_fen_is_complete);
    					}
    					
    					//TODO Implement option to crop image (flag per image)
    					
    					Object forMatching = image;
    					
    					Object cropedProcessedImage = ImageHandlerSingleton.getInstance().resizeImage(forMatching, matcherBoardProperties.getImageSize());
    					
    					//Start matching
    					IMatchingInfo matchingInfo = new MatchingInfo_BaseImpl();
    					startTime = System.currentTimeMillis();
    					
    					//int[][][] rgbBoard = ScannerUtils.convertToRGBMatrix((BufferedImage) cropedProcessedImage);
    					int[][] grayBoard = ScannerUtils.convertToGrayMatrix((BufferedImage) cropedProcessedImage);
    					int[][][] rgbBoard = MatrixUtils.convertTo3ChannelsGray(grayBoard);
    					
    					//ImageHandlerSingleton.getInstance().saveImage("OpenCV_board_" + matcherBoardProperties.getImageSize(), "png", ScannerUtils.createRGBImage(rgbBoard));
    					
    					ResultPair<String, MatchingStatistics> result = matcher.scan(rgbBoard, matchingInfo);
    					
    					String recognized_fen = result.getFirst();
    					
    		            System.out.println(recognized_fen + " " + result.getSecond().totalDelta + " " + (System.currentTimeMillis() - startTime) + "ms");
    					
    		            if (recognized_fen.equals(fen)) {
    		            	
    		            	count_successful++;
    		            }
    				}
    			}
    			
    			text_result += net_names + " variation" + variation + " count_all=" + count_all + " count_successful=" + count_successful + "\r\n";
            }
			
        	System.out.println(text_result);
        	
        	
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	
	private static Map<String, Matcher_Base> createMatchers(
			BoardProperties matcherBoardProperties,
			List<String> netsNames)
					throws FileNotFoundException, ClassNotFoundException, IOException {
		
		Map<String, Matcher_Base> matchers = new HashMap<String, Matcher_Base>();
		
		List<InputStream> netsStreams = createStreams(netsNames);
		
		for (int i = 0; i < netsNames.size(); i++) {
			
			String net_name = netsNames.get(i);
			
			matchers.put(net_name, new Matcher_RGB(new BoardProperties(matcherBoardProperties.getImageSize(), null), net_name, netsStreams.get(i)));
			
		}
		
		return matchers;
	}


	private static List<InputStream> createStreams(List<String> netsNames) throws FileNotFoundException {
		List<InputStream> netsStreams = new ArrayList<InputStream>();
		for (int i = 0; i < netsNames.size(); i++) {
			netsStreams.add(new FileInputStream(netsNames.get(i)));
		}
		return netsStreams;
	}
}
