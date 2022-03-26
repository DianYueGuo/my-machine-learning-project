package neural_network_play_OOXX.network;

import java.util.ArrayList;

public class Network {
	private InputLayer inputLayer;
	private	HiddenLayer hiddenLayers[];
	private OutputLayer outputLayer;
	
	private Edge edges[][][];
	
	public Network(int ... depths) {
		inputLayer = new InputLayer(depths[0]);
		
		hiddenLayers = new HiddenLayer[depths.length - 2];
		for(int i = 0; i < depths.length; i++) {
			hiddenLayers[i] = new HiddenLayer(depths[i]);
		}
		
		outputLayer = new OutputLayer(depths[depths.length - 1]);
		
		
		Edge[][][] gaps = new Edge[depths.length - 1][][];
		for(int i = 0; i < depths.length - 1; i++) {
			Edge edges1[][] = new Edge[depths[i]][];
			for(int j = 0; j < depths[i]; j++) {
				Node begin;
				
				if(i == 0) {
					begin = inputLayer.getNode(j);
				} else { // i <= depths.length - 2
					begin = hiddenLayers[i - 1].getNode(j);
				}
				
				Edge edges2[] = new Edge[depths[i + 1]];
				for(int k = 0; k < depths[i + 1]; k++) {
					Node end;
					
					if(i + 1 < depths.length - 2) {
						end = hiddenLayers[i - 1 + 1].getNode(j);
					} else { // i + 1 == depths.length - 2
						end = outputLayer.getNode(j);
					}
					
					edges2[k] = new Edge(begin, end, 0);
				}
				edges1[j] = edges2;
			}
			gaps[i] = edges1;
		}
	}
}
