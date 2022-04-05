package neural_network;

import java.util.Arrays;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class Network {

	public static enum ACTIVATION_FUNCTION {
		BINARY_STEP, SIGMOID, LINEAR
	}

	public static enum INITIALIZAR {
		RANDOM
	}

	private static class Layer {

		private final Matrix<Double> weights;
		private final Matrix<Double> biases;
		private final ACTIVATION_FUNCTION activationFunction;

		Layer(int width, int inputWidth, ACTIVATION_FUNCTION activationFunction, INITIALIZAR initializar)
				throws InterruptedException {

			this.weights = new Matrix<Double>(width, inputWidth, ((a, b) -> getInitialValue(initializar)),
					Matrix.DOUBLE_CALCULATOR);

			this.biases = new Matrix<Double>(width, 1, ((a, b) -> getInitialValue(initializar)),
					Matrix.DOUBLE_CALCULATOR);

			this.activationFunction = activationFunction;
		}

		public Matrix<Double> getOutput(Matrix<Double> input)
				throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
			return weights.multiply(input).add(biases).map((x) -> getActivationValue(x, activationFunction));
		}

		public Double getWeight(int nodeIndex, int inputNodeIndex) {
			return weights.get(nodeIndex, inputNodeIndex);
		}

		public void setWeight(int nodeIndex, int inputNodeIndex, Double value) {
			weights.set(nodeIndex, inputNodeIndex, value);
		}

		@Override
		public String toString() {
			return "{ weights: " + weights + ", biases: " + biases + ", activationFunction: " + activationFunction
					+ " }";
		}

		public Double getBias(int nodeIndex) {
			return biases.get(nodeIndex, 0);
		}

		public void setBias(int nodeIndex, Double value) {
			biases.set(nodeIndex, 0, value);
		}

	}

	private static class InputLayer extends Layer {

		InputLayer() throws InterruptedException {
			super(0, 0, null, null);
		}

		public Matrix<Double> getOutput(Matrix<Double> input) {
			return input;
		}

	}

	private final Layer layers[];

	Network(INITIALIZAR initializar, ACTIVATION_FUNCTION activationFunctions[], int widths[])
			throws InterruptedException {
		layers = new Layer[widths.length];

		for (int i = 0; i < layers.length; i++) {
			if (i == 0) {
				layers[i] = new InputLayer();
			} else {
				layers[i] = new Layer(widths[i], widths[i - 1], activationFunctions[i], initializar);
			}
		}
	}
	
	public Matrix<Double> getOutput(Matrix<Double> input)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		Matrix<Double> output = input;

		for (int i = 1; i < layers.length; i++) {
			output = layers[i].getOutput(output);
		}

		return output;
	}

	public Double getWeight(int layerIndex, int nodeIndex, int inputNodeIndex) {
		return layers[layerIndex].getWeight(nodeIndex, inputNodeIndex);
	}

	public void setWeight(int layerIndex, int nodeIndex, int inputNodeIndex, Double value) {
		layers[layerIndex].setWeight(nodeIndex, inputNodeIndex, value);
	}

	public Double getBias(int layerIndex, int nodeIndex) {
		return layers[layerIndex].getBias(nodeIndex);
	}

	public void setBias(int layerIndex, int nodeIndex, Double value) {
		layers[layerIndex].setBias(nodeIndex, value);
	}

	@Override
	public String toString() {
		return "{ layers: " + Arrays.toString(layers) + " }";
	}

	private static Double getActivationValue(Double x, ACTIVATION_FUNCTION activationFunction) {
		switch (activationFunction) {
		case BINARY_STEP: {
			if (x < 0) {
				return 0.0;
			} else {
				return 1.0;
			}
		}
		case SIGMOID: {
			return 1 / (1 + Math.exp(-x));
		}
		case LINEAR: {
			return x;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + activationFunction);
		}
	}

	private static Double getInitialValue(INITIALIZAR initializar) {
		switch (initializar) {
		case RANDOM: {
			return Math.random() - 0.5;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + initializar);
		}
	}

}
