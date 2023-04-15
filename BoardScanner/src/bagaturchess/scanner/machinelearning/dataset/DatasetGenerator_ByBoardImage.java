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
	
	
	public static void main(String[] args) {
		
		try {
			
			
			//genSingle(gen_data_chesscom_set1);
			
			SupervisedData[] all_source_sets = SupervisedData.source_set_all;
			
			for (int i = 0; i < all_source_sets.length; i++) {
				
				gen1Set_In1Dir(all_source_sets[i]);
			}

			
			genAllSets_In1Dir(all_source_sets, "./datasets_deepnetts/dnet_universal_extended/", true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	private static void gen1Set_In1Dir(SupervisedData gen_data) throws IOException {
			
		BoardProperties boardProperties = new BoardProperties(256);
		DataSetInitPair[] pairs = DataSetUtils.getInitPairs_Gray(boardProperties, gen_data.input_files, gen_data.fens, gen_data.output_dir, gen_data.flag1);
		
		final List<Object> images = new ArrayList<Object>();
		final List<Integer> pids = new ArrayList<Integer>();
		
		for (int i = 0; i < pairs.length; i++) {
			images.addAll(pairs[i].getImages());
			pids.addAll(pairs[i].getPIDs());
		}
		
		createIndex(gen_data.output_dir);
	}
	
	
	private static void genAllSets_In1Dir(SupervisedData[] gen_data, String output_dir, boolean extend_set) throws IOException {
		
		BoardProperties boardProperties = new BoardProperties(256);
		for (int i = 0; i < gen_data.length; i++) {
			DataSetInitPair[] pairs = DataSetUtils.getInitPairs_Gray(
					boardProperties,
					gen_data[i].input_files,
					gen_data[i].fens,
					output_dir, //gen_data[i].output_dir,
					extend_set
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
