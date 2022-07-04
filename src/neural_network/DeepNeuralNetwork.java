package neural_network;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class DeepNeuralNetwork implements JSONString {

	public static enum ActivationFunction {
		BINARY_STEP, SIGMOID, LINEAR
	}

	public static enum Initializar {
		RANDOM, ZERO
	}

	private static class Layer implements JSONString {

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

		Layer(JSONObject obj) throws JSONException, Exception {
			this.weights = new Matrix<Double>(obj.getJSONObject("weights").getInt("numberOfRows"), obj.getJSONObject("weights").getInt("numberOfColumns"), new BiFunction<Integer, Integer, Double>() {

				@Override
				public Double apply(Integer t, Integer u) {
					return obj.getJSONObject("weights").getJSONArray("values").getJSONArray(t).getDouble(u);
				}
				
			}, Matrix.DOUBLE_CALCULATOR);
			
			this.biases = new Matrix<Double>(obj.getJSONObject("biases").getInt("numberOfRows"), obj.getJSONObject("biases").getInt("numberOfColumns"), new BiFunction<Integer, Integer, Double>() {

				@Override
				public Double apply(Integer t, Integer u) {
					return obj.getJSONObject("biases").getJSONArray("values").getJSONArray(t).getDouble(u);
				}
				
			}, Matrix.DOUBLE_CALCULATOR);

			this.activationFunction = obj.getEnum(ActivationFunction.class, "activationFunction");
		}
		
		private Layer(Layer layer) {
			this.weights = layer.weights.clone();
			this.biases = layer.biases.clone();
			this.activationFunction = layer.activationFunction;
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
			return toJSONString();
		}

		public Layer map(Function<Double, Double> function) throws InterruptedException {
			weights.map(function);
			biases.map(function);

			return this;
		}

		@Override
		public String toJSONString() {
			JSONObject obj = new JSONObject();

			obj.put("weights", this.weights);
			obj.put("biases", this.biases);
			obj.put("activationFunction", this.activationFunction);

			return obj.toString();
		}
		
		@Override
		public Layer clone() {
			return new Layer(this);
		}

	}

	public String name;
	
	protected final Initializar initializar;
	protected final ActivationFunction activationFunction;
	protected final ActivationFunction outputLayerActivationFunction;
	protected final int[] widths;

	private final Layer layers[];

	public DeepNeuralNetwork(Initializar initializar, ActivationFunction activationFunction,
			ActivationFunction outputLayerActivationFunction, int widths[], String name) throws InterruptedException {
		this.initializar = initializar;
		this.activationFunction = activationFunction;
		this.outputLayerActivationFunction = outputLayerActivationFunction;
		this.widths = widths;
		this.name= name;

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

	public DeepNeuralNetwork(JSONObject obj) throws Exception {
		this.initializar = obj.getEnum(Initializar.class, "initializar");
		this.activationFunction = obj.getEnum(ActivationFunction.class, "activationFunction");
		this.outputLayerActivationFunction = obj.getEnum(ActivationFunction.class, "outputLayerActivationFunction");
		this.name = obj.getString("name");
		
		this.widths = new int[obj.getJSONArray("widths").length()];
		for(int i = 0; i < obj.getJSONArray("widths").length(); i++) {
			this.widths[i] = obj.getJSONArray("widths").getInt(i);
		}

		this.layers = new Layer[widths.length];
		for (int i = 0; i < widths.length; i++) {
			if (i == 0) {
				layers[i] = null;
			} else {
				layers[i] = new Layer(obj.getJSONArray("layers").getJSONObject(i));
			}
		}
	}
	
	private DeepNeuralNetwork(DeepNeuralNetwork network) {
		this.initializar = network.initializar;
		this.activationFunction = network.activationFunction;
		this.outputLayerActivationFunction = network.outputLayerActivationFunction;
		this.widths = network.widths.clone();
		this.layers = network.layers.clone();
		this.name = network.name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
		for (int i = 0; i < layers.length; i++) {
			if (i > 0 && i < layers.length - 1) {
				layers[i].map(function);
			}
		}

		return this;
	}

	@Override
	public DeepNeuralNetwork clone() {
		return new DeepNeuralNetwork(this);
	}

	@Override
	public String toJSONString() {
		JSONObject obj = new JSONObject();

		obj.put("initializar", this.initializar);
		obj.put("activationFunction", this.activationFunction);
		obj.put("outputLayerActivationFunction", this.outputLayerActivationFunction);
		obj.put("widths", this.widths);
		obj.put("layers", this.layers);
		obj.put("name", name);

		return obj.toString();
	}
	
	@Override
	public String toString() {
		return toJSONString();
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
		case ZERO: {
			return 0.0;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + initializar);
		}
	}

}
