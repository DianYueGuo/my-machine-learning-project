package neural_network_play_OOXX.network;

public class HiddenLayer extends Layer {
	
	private HiddenNode hiddenNodes[];
	
	public HiddenLayer(int depth) {
		this.hiddenNodes = new HiddenNode[depth];
		
		for(int i = 0; i < depth; i++) {
			this.hiddenNodes[i] = new HiddenNode();
		}
	}
	
	public HiddenNode getNode(int index) {
		return this.hiddenNodes[index];
	}

}
