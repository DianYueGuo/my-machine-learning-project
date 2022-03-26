package neural_network_play_OOXX.network;

public class OutputLayer extends Layer {
	
	private OutputNode outputNodes[];

	public OutputLayer(int depth) {
		this.outputNodes = new OutputNode[depth];
		
		for(int i = 0; i < depth; i++) {
			this.outputNodes[i] = new OutputNode();
		}
	}
	
	public OutputNode getNode(int index) {
		return this.outputNodes[index];
	}
}
