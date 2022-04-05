package neural_network;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;

public class Layer {
	
	private final Matrix<Double> weights;
	private final Matrix<Double> biases;
	private final Function<Double, Double> activationFunction;

	Layer(int width, int inputWidth, Function<Double, Double> activationFunction, Supplier<Double> initializar)
			throws InterruptedException {
		this.weights = new Matrix<Double>(width, inputWidth, new BiFunction<Integer, Integer, Double>() {

			@Override
			public Double apply(Integer t, Integer u) {
				return initializar.get();
			}

		}, Matrix.DOUBLE_CALCULATOR);
		this.biases = new Matrix<Double>(width, 1, new BiFunction<Integer, Integer, Double>() {

			@Override
			public Double apply(Integer t, Integer u) {
				return initializar.get();
			}

		}, Matrix.DOUBLE_CALCULATOR);
		this.activationFunction = activationFunction;
	}

	public Matrix<Double> getOutput(Matrix<Double> input)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		return weights.multiply(input).add(biases).map(activationFunction);
	}
	
	@Override
	public String toString() {
		return "{ weights: " + weights + ", biases: " + biases + ", activationFunction: " + activationFunction + " }";
	}

}
