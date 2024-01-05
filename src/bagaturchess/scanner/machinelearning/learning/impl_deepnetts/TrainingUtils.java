package bagaturchess.scanner.machinelearning.learning.impl_deepnetts;


public class TrainingUtils {
	
	
	public static final float DEFAULT_LEARNING_RATE_DECREASE_PERCENT = 0.5f;
	
	public static final float MIN_LEARNING_RATE 					= TrainingUtils.LEARNING_RATE_100;
	public static final float MAX_LEARNING_RATE 					= TrainingUtils.LEARNING_RATE_10;	
	
	public static final AutoTuningParameters CNN_BOOK_SET1 			= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.028156757f, 0.25f);
	
	public static final AutoTuningParameters CNN_BOOK_SET2 			= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.050056458f, 0.25f);
	
	public static final AutoTuningParameters CNN_BOOK_SET3 			= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.011878632f, 0.25f);
	
	public static final AutoTuningParameters CNN_BOOK_SET4 			= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.011878632f, 0.25f);
	
	public static final AutoTuningParameters CNN_CHESSCOM_SET1 		= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.037542343f, 0.25f);
	
	public static final AutoTuningParameters CNN_CHESSCOM_SET2 		= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.037542343f, 0.25f);
	
	public static final AutoTuningParameters CNN_CHESS24COM_SET1 	= new AutoTuningParameters(2, 2, 1, 2, 2, 9, 0.050056458f, 0.25f);
	
	public static final AutoTuningParameters CNN_LICHESSORG_SET1 	= new AutoTuningParameters(2, 2, 1, 2, 2, 9, 0.06674194f, 0.25f);
	
	public static final AutoTuningParameters CNN_UNIVERSAL 			= new AutoTuningParameters(2, 2, 1, 3, 2, 9, 0.011878632f, 0.25f);
	
	
	public static final int SQUARE_IMAGE_SIZE 						= 32;
	
	public static final long SAVE_NET_FILE_INTERVAL 				= 3 * 60 * 1000;
	
	public static final long MAX_EPOCHS 							= 10000;
	
	public static final float MAX_ERROR_MEAN_CROSS_ENTROPY 			= 0.00001f;
	
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
	
	/*public static final float LEARNING_RATE_1K 						= 0.001f;
	public static final float LEARNING_RATE_2K 						= 0.0005f;
	public static final float LEARNING_RATE_4K 						= 0.00025f;
	public static final float LEARNING_RATE_8K 						= 0.000125f;
	public static final float LEARNING_RATE_10K 					= 0.000125f;
	public static final float LEARNING_RATE_16K 					= 0.0000625f;
	*/
	
	
	public static class AutoTuningParameters {
		
		
		private static final float DEFAULT_MAX_ACCURACY = 1f;
		
		
		public int count_convolutional_layers;
		
		public int convolution_filter_size;
		
		public int has_maxpooling_layer;//0 = false, 1 = true
		
		public int maxpooling_filter_size;
		
		public int maxpooling_filter_stride;
		
		public int size_fully_connected_layer;
		
		public float learning_rate;
		
		public float learning_rate_decrease_percent;
		
		public float max_accuracy;
		
		
		public AutoTuningParameters(int _count_convolutional_layers, int _convolution_filter_size, int _has_maxpooling_layer, int _maxpooling_filter_size, int _maxpooling_filter_stride, int _size_fully_connected_layer, float _learning_rate) {
			
			this(_count_convolutional_layers, _convolution_filter_size, _has_maxpooling_layer, _maxpooling_filter_size, _maxpooling_filter_stride, _size_fully_connected_layer, _learning_rate, DEFAULT_LEARNING_RATE_DECREASE_PERCENT, DEFAULT_MAX_ACCURACY);
		}
		

		public AutoTuningParameters(int _count_convolutional_layers, int _convolution_filter_size, int _has_maxpooling_layer, int _maxpooling_filter_size, int _maxpooling_filter_stride, int _size_fully_connected_layer, float _learning_rate, float _learning_rate_decrease_percent) {
			
			this(_count_convolutional_layers, _convolution_filter_size, _has_maxpooling_layer, _maxpooling_filter_size, _maxpooling_filter_stride, _size_fully_connected_layer, _learning_rate, _learning_rate_decrease_percent, 1f);
		}

		
		public AutoTuningParameters(int _count_convolutional_layers, int _convolution_filter_size, int _has_maxpooling_layer, int _maxpooling_filter_size, int _maxpooling_filter_stride, int _size_fully_connected_layer, float _learning_rate, float _learning_rate_decrease_percent, float _max_accuracy) {
			
			count_convolutional_layers = _count_convolutional_layers;
			
			convolution_filter_size = _convolution_filter_size;
			
			has_maxpooling_layer = _has_maxpooling_layer;
			
			maxpooling_filter_size = _maxpooling_filter_size;
			
			maxpooling_filter_stride = _maxpooling_filter_stride;
			
			size_fully_connected_layer = _size_fully_connected_layer;
			
			learning_rate = _learning_rate;
			
			learning_rate_decrease_percent = _learning_rate_decrease_percent;
			
			max_accuracy = _max_accuracy;
		}
		
		
		@Override
		public String toString() {
			
			if (has_maxpooling_layer == 0) {
				
				return "AutoTuningParameters: [" + count_convolutional_layers + " " + convolution_filter_size + " " + has_maxpooling_layer + size_fully_connected_layer + " " + learning_rate + " " + learning_rate_decrease_percent + "]";
				
			} else {
			
				return "AutoTuningParameters: [" + count_convolutional_layers + " " + convolution_filter_size + " " + has_maxpooling_layer + " " + maxpooling_filter_size + " " + maxpooling_filter_stride + " " + size_fully_connected_layer + " " + learning_rate + " " + learning_rate_decrease_percent + "]";
			}
		}
	}
}
