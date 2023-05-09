package bagaturchess.scanner.machinelearning.dataset;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import bagaturchess.scanner.common.BoardProperties;
import bagaturchess.scanner.machinelearning.SupervisedData;


public class DatasetGenerator_ByBoardImage {
	
	
	private static final float ROTATION_DEGREES 						= 1f;
	
	
	private static final float TRANSLATION_PIXELS_PERCENT				= 2 * 0.033f; //Should be 1 pixel. For image with 32x32 pixels, 0.033 is a bit more than 1.
	
	private static final float TRANSLATION_PIXELS_PERCENT_UNIVERSAL_NET	= 0; //0.033f; //Should be 1 pixel. For image with 32x32 pixels, 0.033 is a bit more than 1.
	
	
	//Use different scales to generate equal amount of images for each dataset
	/*private static final float TRANSLATIONS_RATIO_BOOK_SET1 			= 1.1f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_BOOK_SET2 			= 2.25f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_BOOK_SET3 			= 3f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_CHESSCOM_SET1 		= 1.75f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_CHESSCOM_SET2 		= 5f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_CHESS24COM_SET1 		= 2.25f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_LICHESSORG_SET1 		= 2.25f * TRANSLATION_SCALE;
	
	private static final float TRANSLATIONS_RATIO_UNIVERSAL 			= 0.333f * TRANSLATION_SCALE;

	
	private static final float[] TRANSLATIONS = new float[] {
		TRANSLATIONS_RATIO_BOOK_SET1,
		TRANSLATIONS_RATIO_BOOK_SET2,
		TRANSLATIONS_RATIO_BOOK_SET3,
		TRANSLATIONS_RATIO_CHESSCOM_SET1,
		TRANSLATIONS_RATIO_CHESSCOM_SET2,
		TRANSLATIONS_RATIO_CHESS24COM_SET1,
		TRANSLATIONS_RATIO_LICHESSORG_SET1,
	};
	
	private static final float[] TRANSLATIONS_V2 = new float[] {
		0.033f,
		0.033f,
		0.033f,
		0.033f,
		0.033f,
		0.033f,
		0.033f,
	};
	*/
	
	public static void main(String[] args) {
		
		
		try {
			
			
			//gen1Set_In1Dir(SupervisedData.source_set_all[0], TRANSLATIONS_V2[0], ROTATION_DEGREES);
			
			/*
			SupervisedData[] all_source_sets = [
			gen_data_book_set1,
			gen_data_book_set2,
			gen_data_book_set3,
			gen_data_chesscom_set1,
			gen_data_chesscom_set2,
			gen_data_chess24com_set1,
			gen_data_lichessorg_set1
			] */
			
			SupervisedData[] all_source_sets = SupervisedData.source_set_all;
			
			for (int i = 0; i < all_source_sets.length; i++) {
				
				//gen1Set_In1Dir(all_source_sets[i], TRANSLATIONS[i], ROTATION_DEGREES);
				gen1Set_In1Dir(all_source_sets[i], TRANSLATION_PIXELS_PERCENT, ROTATION_DEGREES);
			}
			
			
			genAllSets_In1Dir(all_source_sets,
					"./datasets_deepnetts/dataset_universal_extended/",
					TRANSLATION_PIXELS_PERCENT_UNIVERSAL_NET,
					ROTATION_DEGREES);
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	
	private static void gen1Set_In1Dir(SupervisedData gen_data, double translations_ration, float rotation_degrees) throws IOException {
			
		BoardProperties boardProperties = new BoardProperties(256);
		
		//boolean extend = gen_data.flag1;
		
		DataSetInitPair[] pairs = DataSetUtils.getInitPairs_Gray(boardProperties, gen_data.input_files, gen_data.fens, gen_data.dataset_dir, translations_ration, rotation_degrees);
		
		final List<Object> images = new ArrayList<Object>();
		final List<Integer> pids = new ArrayList<Integer>();
		
		for (int i = 0; i < pairs.length; i++) {
			images.addAll(pairs[i].getImages());
			pids.addAll(pairs[i].getPIDs());
		}
		
		createIndex(gen_data.dataset_dir);
	}
	
	
	private static void genAllSets_In1Dir(SupervisedData[] gen_data, String output_dir, double translations_ration, float rotation_degrees) throws IOException {
		
		BoardProperties boardProperties = new BoardProperties(256);
		for (int i = 0; i < gen_data.length; i++) {
			
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs_Gray(
					boardProperties,
					gen_data[i].input_files,
					gen_data[i].fens,
					output_dir,
					translations_ration,
					rotation_degrees
					);
			
		}
		
		createIndex(output_dir);
	}
	
	
	private static void createIndex(String output_dir_path) throws FileNotFoundException {
		
		//Create index.txt
		String text_content = "";
		
		File output_dir = new File(output_dir_path);
		File[] labels = output_dir.listFiles();
		
		if (labels != null && labels.length > 0) {
			
			List<String> labels_list = new ArrayList<String>();
			
			for (int i = 0; i < labels.length; i++) {
				
				if (labels[i].isDirectory()) {
						
					String cur_label = labels[i].getName();
					
					labels_list.add(cur_label);
					
					File[] files = labels[i].listFiles();
					
					if (files != null && files.length > 0) {
						
						for (int j = 0; j < files.length; j++) {
							
							String cur_file = files[j].getName();
							
							text_content += cur_label + "\\" + cur_file + " " + cur_label + "\r\n";
						}
					}
				}
			}
		}
        
		PrintWriter out_index = new PrintWriter(output_dir + "/index.txt");
		out_index.println(text_content);
		out_index.close();
		
		//Create labels.txt
		String labels_text = "";
		for (int i = 0; i <= 12; i++) {
			labels_text += i + "\r\n";
		}
		
		PrintWriter out_labels = new PrintWriter(output_dir + "/labels.txt");
		out_labels.println(labels_text);
		out_labels.close();
	}
}
