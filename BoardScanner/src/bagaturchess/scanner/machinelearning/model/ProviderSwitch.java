package bagaturchess.scanner.machinelearning.model;


import java.io.IOException;
import java.io.InputStream;


public class ProviderSwitch {
	
	
	public static String MLFrameworkName = "dl4j";
	
	
	private static ProviderSwitch instance = null;
	
		
	
	private ProviderSwitch() {
		
	}
	
	
	public static ProviderSwitch getInstance() {
		
		if (instance == null) {
			instance = new ProviderSwitch();
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
