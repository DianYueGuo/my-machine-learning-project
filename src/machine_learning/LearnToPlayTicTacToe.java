package machine_learning;

import java.util.Arrays;

import org.json.JSONObject;

import game.tic_tac_toe_game.TicTacToeGame;
import game.tic_tac_toe_game.TicTacToePlayer;
import matrix.MatrixAdditionException;
import matrix.MatrixMultiplicationException;
import neural_network.DeepNeuralNetwork;

public class LearnToPlayTicTacToe extends EvolutionaryLearning {

	@Override
	protected DeepNeuralNetwork[] mutate(DeepNeuralNetwork[] parents) throws InterruptedException {
		final DeepNeuralNetwork[] variants = new DeepNeuralNetwork[variationWidth];

		for (int i = 0; i < variants.length; i++) {
			if (i < selectionWidth) {
				variants[i] = parents[i].clone();
			} else {
				final int index = (int) Math.floor(Math.random() * selectionWidth);
				variants[i] = parents[index].clone();
				variants[i].map(mutationFunction);
				variants[i].setName(variants[i].getName() + "." + i);
			}
		}

		return variants;
	}

	@Override
	protected DeepNeuralNetwork[] select(DeepNeuralNetwork[] variants)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {
		int numberOfMatches = 0;
		int numberOfMarks = 0;

		for (int i = variants.length; i > selectionWidth; i /= 2) {
			shuffleArray(0, i, variants);

			for (int j = 0; j < i / 2; j++) {
				final int index1 = j;
				final int index2 = i - 1 - j;

				TicTacToePlayer player1 = new TicTacToePlayer(variants[index1]);
				TicTacToePlayer player2 = new TicTacToePlayer(variants[index2]);

				TicTacToeGame game = new TicTacToeGame();

				switch (game.match(player1, player2)) {
				case PLAYER1_WIN: {
					break;
				}
				case PLAYER2_WIN: {
					swap(index1, index2, variants);
					break;
				}
				case DRAW: {
					if (Math.random() < 0.5) {
						swap(index1, index2, variants);
					}
					break;
				}
				default:
					throw new IllegalArgumentException();
				}

				numberOfMatches++;
				numberOfMarks += game.getNumberOfMarks();
			}
		}

		fitness = (double) numberOfMarks / numberOfMatches;

		return Arrays.copyOfRange(variants, 0, selectionWidth);
	}

	@Override
	protected void test(DeepNeuralNetwork brain)
			throws InterruptedException, MatrixAdditionException, MatrixMultiplicationException {

		System.out.println("test robot player: ");

		TicTacToePlayer botPlayer = new TicTacToePlayer(brain);
		TicTacToePlayer manPlayer = new TicTacToePlayer(null);

		TicTacToeGame game = new TicTacToeGame();

		System.out.println("botPlayer play first: ");
		System.out.println("result: " + game.match(botPlayer, manPlayer));

		game = new TicTacToeGame();

		System.out.println("manPlayer play first: ");
		System.out.println("result: " + game.match(manPlayer, botPlayer));

	}

	@Override
	protected DeepNeuralNetwork getDeepNeuralNetwork(JSONObject brainJO) throws Exception {
		return new TicTacToePlayer.Brain(brainJO);
	}

	@Override
	protected DeepNeuralNetwork getDeepNeuralNetwork(int[] hidden_layer_depths, String name)
			throws InterruptedException {
		return new TicTacToePlayer.Brain(hidden_layer_depths, name);
	}

	@Override
	protected void initialize(double mutationRate, int variationWidth, int selectionWidth,
			DeepNeuralNetwork[] parents) {
		this.mutationRate = mutationRate;
		this.variationWidth = variationWidth;
		this.selectionWidth = selectionWidth;
		this.mutationFunction = a -> {
			if (Math.random() < mutationRate) {
				return Math.random() * 2 - 1;
			}

			return a;
		};
		this.parents = parents;
	}

	private void shuffleArray(int begin, int end, Object array[]) {
		for (int i = begin; i < end; i++) {
			int index = (int) Math.floor(Math.random() * (end - begin - i));

			swap(i, i + index, array);
		}
	}

	private void swap(int a, int b, Object array[]) {
		Object temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	public static void main(String args[]) throws Exception {
		new LearnToPlayTicTacToe().runEvolutionaryLearning(args);
	}

}
