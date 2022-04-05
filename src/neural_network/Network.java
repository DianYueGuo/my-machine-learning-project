package neural_network;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class Network {

	private final Layer layers[];

	public static final Supplier<Double> RANDOM_INITIALIZAR = new Supplier<Double>() {

		@Override
		public Double get() {
			return Math.random();
		}

	};

	public static final Function<Double, Double> BINARY_STEP_ACTIVATION_FUNCTION = new Function<Double, Double>() {

		@Override
		public Double apply(Double t) {
			if (t < 0) {
				return 0.0;
			} else {
				return 1.0;
			}
		}
		
		@Override
		public String toString() {
			return "BINARY_STEP_ACTIVATION_FUNCTION";
		}

	};
	public static final Function<Double, Double> SIGMOID_ACTIVATION_FUNCTION = new Function<Double, Double>() {

		@Override
		public Double apply(Double t) {
			return 1 / (1 + Math.exp(-t));
		}
		
		@Override
		public String toString() {
			return "SIGMOID_ACTIVATION_FUNCTION";
		}

	};
	public static final Function<Double, Double> LINEAR_ACTIVATION_FUNCTION = new Function<Double, Double>() {

		@Override
		public Double apply(Double t) {
			return t;
		}

		@Override
		public String toString() {
			return "LINEAR_ACTIVATION_FUNCTION";
		}
		
	};

	public Network(Supplier<Double> initializar, Function<Double, Double> activationFunction, int widths[])
			throws InterruptedException {
		layers = new Layer[widths.length];

		for (int i = 0; i < layers.length; i++) {
			if (i == 0) {
				layers[i] = new InputLayer();
			} else {
				layers[i] = new Layer(widths[i], widths[i - 1], activationFunction, initializar);
			}
		}
	}

	public Matrix<Double> getOutput(Matrix<Double> input)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		Matrix<Double> output = null;

		for (int i = 0; i < layers.length; i++) {
			output = layers[i].getOutput(input);
		}

		return output;
	}
	
	@Override
	public String toString() {
		return "{ layers: " + Arrays.toString(layers) + " }";
	}

}
