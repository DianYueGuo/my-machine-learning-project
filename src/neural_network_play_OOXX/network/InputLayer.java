package neural_network_play_OOXX.network;

public class InputLayer extends Layer {
	
	private InputNode inputNodes[];

	public InputLayer(int depth) {
		this.inputNodes = new InputNode[depth];
		
		for(int i = 0; i < depth; i++) {
			this.inputNodes[i] = new InputNode();
		}
	}
	
	public InputNode getNode(int index) {
		return this.inputNodes[index];
	}
}
