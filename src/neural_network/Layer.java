package neural_network;

import java.util.function.Function;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class Layer {

	private final Matrix<Double> weights;
	private final Matrix<Double> biases;
	private final Function<Double, Double> activationFunction;

	private Matrix<Double> input;

	Layer(int width, int inputWidth, Function<Double, Double> activationFunction) {
		this.weights = new Matrix<Double>(width, inputWidth, Matrix.DOUBLE_CALCULATOR);
		this.biases = new Matrix<Double>(width, 1, Matrix.DOUBLE_CALCULATOR);
		this.activationFunction = activationFunction;
	}

	public void input(Matrix<Double> input) {
		this.input = input;
	}
	
	public Matrix<Double> getOutput() throws MatrixAdditionException, MatrixMultiplicationException {
		return weights.multiply(input).add(biases).map(activationFunction);
	}

}
