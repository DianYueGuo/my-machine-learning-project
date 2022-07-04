package machine_learning;

import java.util.function.Function;

import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public abstract class EvolutionaryLearning {

	protected final double mutationRate;
	protected final Function<Double, Double> mutationFunction;
	protected int numberOfVariantsToCreate;
	protected int selectionWidth;
	protected DeepNeuralNetwork[] parents;

	public EvolutionaryLearning(double mutationRate, int numberOfVariantsToCreate, int selectionWidth,
			Function<Double, Double> mutationFunction, DeepNeuralNetwork[] parents) {
		this.mutationRate = mutationRate;
		this.numberOfVariantsToCreate = numberOfVariantsToCreate;
		this.selectionWidth = selectionWidth;
		this.mutationFunction = mutationFunction;
		this.parents = parents;
	}

	public void update() throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		final DeepNeuralNetwork[] variants = mutate(parents);
		parents = select(variants);
	}

	protected abstract DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException;

	protected abstract DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException;

}
