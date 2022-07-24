package machine_learning;

import java.util.concurrent.ThreadLocalRandom;

import org.jfree.chart.ui.ApplicationFrame;
import org.json.JSONObject;

import matrix.Matrix;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;
import neural_network.DeepNeuralNetwork.Initializar;

public class LearnToMatchSinFunction extends EvolutionaryLearning {
	
	private class ShowChart extends ApplicationFrame {

		public ShowChart(String title) {
			super(title);
		}
		
	}

	@Override
	protected DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException {
		final DeepNeuralNetwork[] variants = new DeepNeuralNetwork[getVariationWidth()];

		for (int i = 0; i < variants.length; i++) {
			if (i < getSelectionWidth()) {
				variants[i] = parents[i].clone();
			} else {
				final int index = (int) Math.floor(Math.random() * getSelectionWidth());
				variants[i] = parents[index].clone();
				variants[i].map(a -> {
					if (ThreadLocalRandom.current().nextDouble() < getMutationRate()) {
						return ThreadLocalRandom.current().nextDouble() * 2 - 1;
					}

					return a;
				});
				variants[i].setName(variants[i].getName() + "." + i);
			}
		}

		return variants;
	}

	@Override
	protected DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		Double RMSs[] = new Double[variants.length];

		for (int i = 0; i < variants.length; i++) {
			final int numberOfSamples = 100;
			double sumOfSquares = 0;

			for (double x = 0; x <= 2 * Math.PI; x += 2 * Math.PI / numberOfSamples) {
				final double y = variants[i]
						.getOutput(new Matrix<Double>(new Double[][] { { x } }, 1, 1, Matrix.DOUBLE_CALCULATOR))
						.get(0, 0);
				sumOfSquares += (y - Math.sin(x)) * (y - Math.sin(x));
			}

			RMSs[i] = Math.sqrt(sumOfSquares / numberOfSamples);
		}

		for (int i = 0; i < RMSs.length; i++) {
			for (int j = 0; j < getSelectionWidth(); j++) {
				if (RMSs[i] < RMSs[j]) {
					swap(i, j, RMSs);
					swap(i, j, variants);
				}
			}
		}

		DeepNeuralNetwork selecteDeepNeuralNetworks[] = new DeepNeuralNetwork[getSelectionWidth()];
		for (int i = 0; i < selecteDeepNeuralNetworks.length; i++) {
			selecteDeepNeuralNetworks[i] = variants[i];
		}
		
		double sum = 0;
		for (int i = 0; i < RMSs.length; i++) {
			sum += RMSs[i];
		}
		setFitness(1 / (1 + sum / RMSs.length));

		return selecteDeepNeuralNetworks;
	}

	@Override
	protected void test(DeepNeuralNetwork brain)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	protected DeepNeuralNetwork getDeepNeuralNetwork(JSONObject brainJO) throws Exception {
		return new DeepNeuralNetwork(brainJO);
	}

	@Override
	protected DeepNeuralNetwork getDeepNeuralNetwork(int[] hidden_layer_depths, String name, Initializar initializar)
			throws InterruptedException {
		int[] layer_depths = new int[hidden_layer_depths.length + 2];

		layer_depths[0] = 1;

		for (int i = 0; i < hidden_layer_depths.length; i++) {
			layer_depths[i + 1] = hidden_layer_depths[i];
		}

		layer_depths[hidden_layer_depths.length - 2] = 1;

		return new DeepNeuralNetwork(initializar, DeepNeuralNetwork.ActivationFunction.SIGMOID,
				DeepNeuralNetwork.ActivationFunction.LINEAR, hidden_layer_depths, name);
	}

	private void swap(int a, int b, Object array[]) {
		Object temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	public static void main(String args[]) throws Exception {
		new LearnToMatchSinFunction().runEvolutionaryLearning(args);
	}

}
