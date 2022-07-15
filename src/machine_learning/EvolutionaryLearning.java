package machine_learning;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import org.json.JSONObject;

import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public abstract class EvolutionaryLearning {

	private double mutationRate;
	private int variationWidth;
	private int selectionWidth;
	private DeepNeuralNetwork[] parents;
	private double fitness;
	
	protected int getVariationWidth() {
		return variationWidth;
	}

	protected void setVariationWidth(int variationWidth) {
		this.variationWidth = variationWidth;
	}

	protected int getSelectionWidth() {
		return selectionWidth;
	}

	protected void setSelectionWidth(int selectionWidth) {
		this.selectionWidth = selectionWidth;
	}

	protected double getMutationRate() {
		return mutationRate;
	}

	protected void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	protected double getFitness() {
		return fitness;
	}

	protected void setFitness(double fitness) {
		this.fitness = fitness;
	}

	protected abstract DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException;

	protected abstract DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException;

	protected abstract void test(DeepNeuralNetwork brain)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException;

	protected abstract DeepNeuralNetwork getDeepNeuralNetwork(JSONObject brainJO) throws Exception;

	protected abstract DeepNeuralNetwork getDeepNeuralNetwork(int[] hidden_layer_depths, String name)
			throws InterruptedException;

	protected abstract Function<Double, Double> getMutationFunction();
	
	private void update() throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		final DeepNeuralNetwork[] variants = mutate(parents);
		this.parents = select(variants);
	}
	
	protected final void runEvolutionaryLearning(String args[]) throws Exception {

		if (args.length >= 7 && args[0].equals("train")) {

			String name = args[1];

			final int selectionWidth = Integer.parseInt(args[args.length - 2]);

			DeepNeuralNetwork brains[] = new DeepNeuralNetwork[selectionWidth];
			for (int i = 0; i < selectionWidth; i++) {
				JSONObject brainJO = new JSONObject(Files.readString(Path.of(args[2 + i])));
				DeepNeuralNetwork brain = getDeepNeuralNetwork(brainJO);
				brains[i] = brain;
			}

			final double learningRate = Double.parseDouble(args[args.length - 4]);
			final int variationWidth = Integer.parseInt(args[args.length - 3]);
			final int updateTimes = Integer.parseInt(args[args.length - 1]);

			PrintWriter pwLog = new PrintWriter(name + ".log");
			pwLog.println("command, name, learningRate, variationWidth, selectionWidth, updateTimes");
			pwLog.println("train, " + name + ", " + learningRate + ", " + variationWidth + ", " + selectionWidth + ", "
					+ updateTimes);
			pwLog.println("update, fitness");
			pwLog.flush();

			setMutationRate(learningRate);
			setVariationWidth(variationWidth);
			setSelectionWidth(selectionWidth);
			this.parents = brains;

			long previousTime = System.currentTimeMillis();
			long currentTime = previousTime;
			for (int i = 1; i <= updateTimes; i++) {

				// update
				update();

				// print estimated time
				currentTime = System.currentTimeMillis();
				System.out.println("update " + i + " (fitness: " + Math.round(getFitness() * 1000) / 1000.0 + ", progress: "
						+ Math.round(((double) i / updateTimes * 100) * 10) / 10.0 + "%, "
						+ Math.round(((currentTime - previousTime) * (updateTimes - i) / 60000.0) * 10) / 10.0
						+ " minutes left)");
				previousTime = currentTime;

				// make filename
				String filenames[] = new String[selectionWidth];
				for (int j = 0; j < selectionWidth; j++) {
					filenames[j] = name + "_" + (j + 1) + "." + i + ".json";
				}

				// save file
				for (int j = 0; j < selectionWidth; j++) {
					FileWriter fw = new FileWriter(filenames[j]);
					fw.write(parents[j].toJSONString());
					fw.close();
				}

				// add log message
				pwLog.println(i + ", " + Math.round(getFitness() * 1000) / 1000.0);
				pwLog.flush();

				// print save message
				System.out.println("the brains are saved as " + Arrays.toString(filenames));

			}

			// close log file
			pwLog.close();

		} else if (args.length == 2 && args[0].equals("test")) {

			test(getDeepNeuralNetwork(new JSONObject(Files.readString(Path.of(args[1])))));

		} else if (args.length >= 2 && args[0].equals("create")) {

			int[] hiddenLayerDepths = new int[args.length - 2];

			for (int i = 0; i < hiddenLayerDepths.length; i++) {
				hiddenLayerDepths[i] = Integer.valueOf(args[i + 2]);
			}

			String name = args[1];

			DeepNeuralNetwork brain = getDeepNeuralNetwork(hiddenLayerDepths, name + ".0");

			String filename = name + ".json";

			FileWriter fw = new FileWriter(filename);
			fw.write(brain.toJSONString());
			fw.close();

			System.out.println("new brain \"" + filename + "\" is created!");

		} else {
			System.out.println("Syntax error");
			System.out.println("The syntax should be: ");
			System.out.println(
					"\"train <name> <brain1> <brain2>...<brainN> <learningRate> <variationWidth> <selectionWidth> <updateTimes>\" (selectionWidth should be equivalent to the number of <brain> and be less than or equal to variationWidth)");
			System.out.println("or");
			System.out.println("\"test <name>\"");
			System.out.println("or");
			System.out.println("\"create <name> <d1> <d2> <d3>...\" ('d' represents the depth of a hidden layer)");
		}

	}

}
