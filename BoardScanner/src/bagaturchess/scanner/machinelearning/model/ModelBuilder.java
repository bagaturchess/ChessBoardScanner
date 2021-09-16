package bagaturchess.scanner.machinelearning.model;

import java.io.IOException;
import java.io.InputStream;

public class ModelBuilder {
	
	
	private static final String MLFrameworkName = "dl4j";
	
	
	private static ModelBuilder instance = null;
	
		
	
	private ModelBuilder() {
		
	}
	
	
	public static ModelBuilder getInstance() {
		
		if (instance == null) {
			instance = new ModelBuilder();
		}
		
		return instance;
	}
	
	
	public NetworkModel create(int channelsCount, InputStream networkFileStream, int _squareSize) throws ClassNotFoundException, IOException {
		if ("deepnetts".equals(MLFrameworkName)) {
			if (channelsCount == 1) {
				return new bagaturchess.scanner.machinelearning.learning.impl_deepnetts.model.NetworkModel_Gray(networkFileStream, _squareSize);
			} else if (channelsCount == 3) {
				return new bagaturchess.scanner.machinelearning.learning.impl_deepnetts.model.NetworkModel_RGB(networkFileStream, _squareSize);
			} else {
				throw new IllegalStateException("Unsupported channels count " + channelsCount);
			}
		} else if ("dl4j".equals(MLFrameworkName)) {
			if (channelsCount == 1) {
				return new bagaturchess.scanner.machinelearning.learning.impl_dl4j.model.NetworkModel_Gray(networkFileStream, _squareSize);
			} else if (channelsCount == 3) {
				//return new bagaturchess.scanner.machinelearning.learning.impl_dl4j.model.NetworkModel_RGB(networkFileStream, _squareSize);
				throw new IllegalStateException("[dl4j impl] Unsupported channels count " + channelsCount);
			} else {
				throw new IllegalStateException("Unsupported channels count " + channelsCount);
			}
		} else {
			throw new IllegalStateException("Unsupported ML implementation with name " + MLFrameworkName);
		}
	}
}
