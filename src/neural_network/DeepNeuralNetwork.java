package neural_network;

import java.util.Arrays;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class DeepNeuralNetwork {

	public static enum ActivationFunction {
		BINARY_STEP, SIGMOID, LINEAR
	}

	public static enum Initializar {
		RANDOM
	}

	private static class Layer {

		private final Matrix<Double> weights;
		private final Matrix<Double> biases;
		private final ActivationFunction activationFunction;

		Layer(int width, int inputWidth, ActivationFunction activationFunction, Initializar initializar)
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

		public Double getBias(int nodeIndex) {
			return biases.get(nodeIndex, 0);
		}

		public void setBias(int nodeIndex, Double value) {
			biases.set(nodeIndex, 0, value);
		}

		@Override
		public String toString() {
			return "{ weights: " + weights + ", biases: " + biases + ", activationFunction: " + activationFunction
					+ " }";
		}

		public Layer map(Function<Double, Double> function) throws InterruptedException {
			weights.map(function);
			biases.map(function);

			return this;
		}

	}

	protected final Initializar initializar;
	protected final ActivationFunction activationFunction;
	protected final ActivationFunction outputLayerActivationFunction;
	protected final int[] widths;

	private final Layer layers[];

	public DeepNeuralNetwork(Initializar initializar, ActivationFunction activationFunction,
			ActivationFunction outputLayerActivationFunction, int widths[]) throws InterruptedException {
		this.initializar = initializar;
		this.activationFunction = activationFunction;
		this.outputLayerActivationFunction = outputLayerActivationFunction;
		this.widths = widths;

		layers = new Layer[widths.length];

		for (int i = 0; i < widths.length; i++) {
			if (i == 0) {
				layers[i] = null;
			} else if (i < widths.length - 1) {
				layers[i] = new Layer(widths[i], widths[i - 1], activationFunction, initializar);
			} else {
				layers[i] = new Layer(widths[i], widths[i - 1], outputLayerActivationFunction, initializar);
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

	public int[] getSize() {
		return widths;
	}

	public DeepNeuralNetwork map(Function<Double, Double> function) throws InterruptedException {
		for (int i = 0; i < widths.length; i++) {
			if (i > 0 && i < widths.length - 1) {
				layers[i].map(function);
			}
		}

		return this;
	}

	@Override
	public String toString() {
		return "{ layers: " + Arrays.toString(layers) + " }";
	}

	@Override
	public DeepNeuralNetwork clone() {
		DeepNeuralNetwork returnValue = null;
		try {
			returnValue = new DeepNeuralNetwork(initializar, activationFunction, outputLayerActivationFunction, widths);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}

	private static Double getActivationValue(Double x, ActivationFunction activationFunction) {
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

	private static Double getInitialValue(Initializar initializar) {
		switch (initializar) {
		case RANDOM: {
			return Math.random() * 2 - 1;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + initializar);
		}
	}

}
