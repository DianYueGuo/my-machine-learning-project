package neural_network;

import matrix.Matrix;

public class InputLayer extends Layer {

	InputLayer() throws InterruptedException {
		super(0, 0, null, null);
	}

	
	public Matrix<Double> getOutput(Matrix<Double> input){
		return input;
	}
	
}
