package bagaturchess.scanner.machinelearning.learning.impl_deepnetts;


public class TrainingUtils {

	
	public static final int SQUARE_IMAGE_SIZE 						= 32;
	
	public static final long SAVE_NET_FILE_INTERVAL 				= 3 * 60 * 1000;
	
	public static final float MAX_ERROR_MEAN_CROSS_ENTROPY 			= 0.00001f;
	
	public static final long MAX_EPOCHS 							= 10000;
	
	public static final float MAX_ERROR_MEAN_SQUARED_ERROR 			= MAX_ERROR_MEAN_CROSS_ENTROPY / 1000f;
	
	public static final float LEARNING_RATE_1 						= 1f;
	public static final float LEARNING_RATE_2 						= 0.5f;
	public static final float LEARNING_RATE_4 						= 0.25f;
	public static final float LEARNING_RATE_10 						= 0.1f;
	public static final float LEARNING_RATE_15 						= 0.067f;
	public static final float LEARNING_RATE_20 						= 0.05f;
	public static final float LEARNING_RATE_50 						= 0.02f;
	public static final float LEARNING_RATE_100 					= 0.01f;
	public static final float LEARNING_RATE_200 					= 0.005f;
	public static final float LEARNING_RATE_400 					= 0.0025f;
	public static final float LEARNING_RATE_800 					= 0.00125f;
	
	public static final float LEARNING_RATE_1K 						= 0.001f;
	public static final float LEARNING_RATE_2K 						= 0.0005f;
	public static final float LEARNING_RATE_4K 						= 0.00025f;
	public static final float LEARNING_RATE_8K 						= 0.000125f;
	public static final float LEARNING_RATE_10K 					= 0.000125f;
	public static final float LEARNING_RATE_16K 					= 0.0000625f;
	
	
	public static class AutoTuningParameters {
		
		
		public int count_convolutional_layers;
		
		public int size_fully_connected_layer;
		
		public float learning_rate;
		
		
		public AutoTuningParameters(int _count_convolutional_layers, int _size_fully_connected_layer, float _learning_rate) {
			
			count_convolutional_layers = _count_convolutional_layers;
			
			size_fully_connected_layer = _size_fully_connected_layer;
			
			learning_rate = _learning_rate;
		}
		
		
		@Override
		public String toString() {
			
			return "AutoTuningParameters: [" + count_convolutional_layers + "	" + size_fully_connected_layer + "	" + learning_rate + "]";
		}
	}
}
