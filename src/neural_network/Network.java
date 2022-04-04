package neural_network;

public class Network {

	private Layer layers[];
	
	Network(int... widths) {
		layers = new Layer[widths.length];
		
		for(int i = 0; i < layers.length; i++) {
			layers[i] = new Layer(widths[i], widths[i - 1], null)
		}
	}
	
}
